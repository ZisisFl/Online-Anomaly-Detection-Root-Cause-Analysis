package anomaly_detection.detectors

import anomaly_detection.AnomalyDetector
import anomaly_detection.aggregators.SumAggregator
import models.{AggregatedRecords, AggregatedRecordWithBaseline, InputRecord}
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

  override def runDetection(env: StreamExecutionEnvironment): DataStream[AggregatedRecordWithBaseline] = {
    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1,
      "earliest"
    )

    inputStream
      .assignAscendingTimestamps(record => record.epoch)
      //.map(record => mapRecordToAnomaly(record))
      .windowAll(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      //.windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5))) processing time alternative
      .aggregate(new SumAggregator)
      //apply upper limit threshold rule, needs a fix
      .filter(agg_record => (valueTooLow(agg_record.current) || valueTooHigh(agg_record.current)))
      //apply lower limit threshold rule
      //.filter(agg_record => valueTooHigh(agg_record.current))
      .map(agg_record => AggregatedRecordWithBaseline(
        current = agg_record.current,
        baseline = computeBaseline(agg_record.current),
        input_records = agg_record.input_records))
  }

  private def valueTooHigh(value: Double): Boolean = {
    value > spec.max
  }

  private def valueTooLow(value: Double): Boolean = {
    value < spec.min
  }

  private def findAnomalies(value: Double): Boolean = {
    if (valueTooLow(value)) false
    else if (valueTooHigh(value)) false
    else true
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
