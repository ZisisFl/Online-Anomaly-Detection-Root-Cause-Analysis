package root_cause_analysis

import anomaly_detection.detectors.{ThresholdDetector, ThresholdDetectorSpec}
import config.AppConfig
import models.{AnomalyEvent, InputRecord}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sources.kafka.InputRecordStreamBuilder

class HierarchicalContributorsFinderTest extends AnyFlatSpec with Matchers {
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "anomaly detection and rca" should "work" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.aggregationWindowSize = 30

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1)

    val output: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    val hierarchicalContributorsFinder = new HierarchicalContributorsFinder()

    hierarchicalContributorsFinder.runSearch(output).print()

    env.execute("Hierarchical Contributors Finder test")
  }

  "manual anomaly detection and rca" should "work" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.aggregationWindowSize = 30

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1)

    val output: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    val hierarchicalContributorsFinder = new HierarchicalContributorsFinder()

    output
      // map stream of AnomalyEvent to stream of (DimensionGroup, AnomalyEvent)
      .flatMap(record => hierarchicalContributorsFinder.keyByDimensionGroup(record))
      .map(anomaly => (anomaly._1, hierarchicalContributorsFinder.search(anomaly._2)))
      .print()

    env.execute("Hierarchical Contributors Finder test")
  }
}
