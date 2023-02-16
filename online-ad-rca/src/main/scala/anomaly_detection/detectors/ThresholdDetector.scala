package anomaly_detection.detectors

import anomaly_detection.AnomalyDetector
import anomaly_detection.aggregators.{OffsetBaselineAggregator, SumAggregator}
import models.{AggregatedRecords, AggregatedRecordsWBaseline, AnomalyEvent, InputRecord}
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

class ThresholdDetector extends AnomalyDetector[ThresholdDetectorSpec] {
  private var spec: ThresholdDetectorSpec = _

  override def init(spec: ThresholdDetectorSpec): Unit = {
    if (spec.min >= spec.max) {
      throw new ArithmeticException("You cannot set a min threshold higher or equal to max threshold")
    }
    this.spec = spec
  }

  override def runDetection(inputStream: DataStream[InputRecord]): DataStream[AnomalyEvent] = {

    // aggregation
    val aggregatedRecordsStream: DataStream[AggregatedRecords] = inputStream
      .assignAscendingTimestamps(_.epoch)
      .windowAll(SlidingEventTimeWindows.of(Time.seconds(spec.aggregationWindowSize), Time.seconds(spec.aggregationWindowSlide)))
//      .windowAll(TumblingEventTimeWindows.of(Time.seconds(spec.aggregationWindowSize)))
      .aggregate(new SumAggregator)
//    aggregatedRecordsStream.print()

    // baseline
    val aggregatedRecordsWBaselineStream: DataStream[AggregatedRecordsWBaseline] = aggregatedRecordsStream
      .countWindowAll(spec.elementsInBaselineOffsetWindow, 1)
      .aggregate(new OffsetBaselineAggregator)
//    aggregatedRecordsWBaselineStream.print()

    // anomaly detection
    val anomalyEventStream: DataStream[AnomalyEvent] = aggregatedRecordsWBaselineStream
      .filter(record => isAnomaly(record.current))
      .map(record => AnomalyEvent(record))

    anomalyEventStream
  }

  private def valueTooHigh(value: Double): Boolean = {
    value > spec.max
  }

  private def valueTooLow(value: Double): Boolean = {
    value < spec.min
  }

  /**
   * This could take as input a AggregatedRecords object and function could be abstract in Anomaly Detector trait
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
