package anomaly_detection.detectors

import anomaly_detection.AnomalyDetector
import anomaly_detection.aggregators.{SumAggregator, OffsetBaselineAggregator}
import models.accumulators.OffsetBaselineAccumulator
import models.{AggregatedRecords, InputRecord}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, SlidingProcessingTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import sources.kafka.InputRecordStreamBuilder

class ThresholdDetector extends AnomalyDetector[ThresholdDetectorSpec] {
  private var spec: ThresholdDetectorSpec = _
//  private var env: StreamExecutionEnvironment = _
  override def init(spec: ThresholdDetectorSpec): Unit = {
    if (spec.min >= spec.max) {
      throw new ArithmeticException("You cannot set a min threshold higher or equal to max threshold")
    }

    this.spec = spec
//    this.env = env
  }

  override def runDetection(env: StreamExecutionEnvironment): Unit = {
    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1,
      "earliest"
    )

    val aggregationWindowSize = 30
    val aggregationWindowSlide = 10

    val numberOfOffsetWindows = 5
    val rootCauseLookback = aggregationWindowSize * numberOfOffsetWindows

    inputStream
      .assignAscendingTimestamps(record => record.epoch)
      .windowAll(SlidingEventTimeWindows.of(Time.seconds(aggregationWindowSize), Time.seconds(aggregationWindowSlide)))
      //.windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5))) processing time alternative
      .aggregate(new SumAggregator)
      .assignAscendingTimestamps(agg_record => agg_record.window_starting_epoch)
      //      .map(agg_record => (isAnomaly(agg_record.current), agg_record))
      .windowAll(SlidingEventTimeWindows.of(Time.seconds(rootCauseLookback), Time.seconds(aggregationWindowSize)))
      .aggregate(new OffsetBaselineAggregator)
      //.map(record => mapRecordToAnomaly(record))
      .print()
  }

  private def valueTooHigh(value: Double): Boolean = {
    value > spec.max
  }

  private def valueTooLow(value: Double): Boolean = {
    value < spec.min
  }

  private def isAnomaly(value: Double): Boolean = {
    if (valueTooLow(value)) true
    else if (valueTooHigh(value)) true
    else false
  }

  private def computeBaseline(current: Double): Double = {
    var baseline = current
    if (valueTooLow(current)) {
      baseline = spec.min
    }
    else if (valueTooHigh(current)) {
      baseline = spec.max
    }
    baseline
  }
}
