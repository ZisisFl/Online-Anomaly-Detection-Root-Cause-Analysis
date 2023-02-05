package anomaly_detection.detectors

import anomaly_detection.AnomalyDetector
import anomaly_detection.aggregators.{OffsetBaselineAggregator, SumAggregator}
import models.{AggregatedRecordsWBaseline, InputRecord}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import sources.kafka.InputRecordStreamBuilder

class ThresholdDetector extends AnomalyDetector[ThresholdDetectorSpec] {
  private var spec: ThresholdDetectorSpec = _

  override def init(spec: ThresholdDetectorSpec): Unit = {
    if (spec.min >= spec.max) {
      throw new ArithmeticException("You cannot set a min threshold higher or equal to max threshold")
    }

    this.spec = spec
  }

  override def runDetection(env: StreamExecutionEnvironment): DataStream[AggregatedRecordsWBaseline] = {
    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1,
      "earliest"
    )

    val aggregationWindowSize = 30
    val aggregationWindowSlide = 10

    val numberOfOffsetWindows = 2
    val rootCauseWindowSize = aggregationWindowSize * numberOfOffsetWindows

    inputStream
      .assignAscendingTimestamps(record => record.epoch)
      .windowAll(SlidingEventTimeWindows.of(Time.seconds(aggregationWindowSize), Time.seconds(aggregationWindowSlide)))
      .aggregate(new SumAggregator)
      .assignAscendingTimestamps(agg_record => agg_record.window_starting_epoch)
      .windowAll(SlidingEventTimeWindows.of(Time.seconds(rootCauseWindowSize), Time.seconds(aggregationWindowSize)))
      .aggregate(new OffsetBaselineAggregator)
      .filter(record => isAnomaly(record.current))
//      .map(record => (isAnomaly(record.current), record))
  }

  private def valueTooHigh(value: Double): Boolean = {
    value > spec.max
  }

  private def valueTooLow(value: Double): Boolean = {
    value < spec.min
  }

  /**
   * This could take as input a AggregatedRecords object and function could be abstract in Anomalyt Detector trait
   * @param value
   * @return
   */
  private def isAnomaly(value: Double): Boolean = {
    if (valueTooLow(value)) true
    else if (valueTooHigh(value)) true
    else false
  }

  /**
   * For Threshold rule baseline is the actual value or in cases that value exceeds threshold
   * limits the value of the relevant boundary (upper or low)
   * @param value
   * @return
   */
  private def computeBaseline(value: Double): Double = {
    if (valueTooLow(value)) spec.min
    else if (valueTooHigh(value)) spec.max
    else value
  }
}
