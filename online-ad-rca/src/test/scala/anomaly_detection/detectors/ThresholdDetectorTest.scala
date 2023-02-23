package anomaly_detection.detectors

import anomaly_detection.aggregators.{OffsetBaselineAggregator, SumAggregator}
import config.AppConfig
import models.{AggregatedRecords, InputRecord}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.scalatest.flatspec.AnyFlatSpec
import sources.kafka.InputRecordStreamBuilder

class ThresholdDetectorTest extends AnyFlatSpec{
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "test threshold detector" should "detect anomalies" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.aggregationWindowSize = 30
    spec.elementsInBaselineOffsetWindow = 10

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      env,
      1
    )

    detector.runDetection(inputStream)
      .print()

    env.execute("ThresholdDetector test")
  }

  "manual test threshold detector" should "detect anomalies" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f


    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      env,
      1
    )

    val aggregationWindowSize = 30
    val aggregationWindowSlide = 60

    val aggregatedRecordsStream: DataStream[AggregatedRecords] = inputStream
      .assignAscendingTimestamps(_.epoch)
//      .windowAll(SlidingEventTimeWindows.of(Time.seconds(aggregationWindowSize), Time.seconds(aggregationWindowSlide)))
      .windowAll(TumblingEventTimeWindows.of(Time.seconds(aggregationWindowSize)))
      .aggregate(new SumAggregator)

    val aggregatedRecordsWBaselineStream = aggregatedRecordsStream
      .countWindowAll(10, 1)
      .aggregate(new OffsetBaselineAggregator)
    aggregatedRecordsWBaselineStream.print()

//    val output: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    //    output.print()

    env.execute("ThresholdDetector test")
  }
}