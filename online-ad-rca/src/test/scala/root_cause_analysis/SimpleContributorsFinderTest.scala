package root_cause_analysis

import anomaly_detection.detectors.{ThresholdDetector, ThresholdDetectorSpec}
import config.AppConfig
import models.{AnomalyEvent, InputRecord}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sinks.kafka.RCAResultJsonProducer
import sources.kafka.InputRecordStreamBuilder

class SimpleContributorsFinderTest extends AnyFlatSpec with Matchers {
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
      env,
      1)

    val output: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    val simpleContributorsFinder = new SimpleContributorsFinder()

    simpleContributorsFinder
      .runSearch(output)
      .print()

    env.execute("Simple Contributors Finder test")
  }

  "anomaly detection and rca with kafka sink" should "work" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.aggregationWindowSize = 30

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      env,
      1)

    val output: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    val simpleContributorsFinder = new SimpleContributorsFinder()

    simpleContributorsFinder
      .runSearch(output)
      .addSink(RCAResultJsonProducer())

    env.execute("Simple Contributors Finder test")
  }

  "manual anomaly detection and rca" should "work" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      env,
      1)

    val output: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    val simpleContributorsFinder = new SimpleContributorsFinder()

    output
      .map(anomaly => simpleContributorsFinder.search(anomaly))
      .print()

    env.execute("Simple Contributors Finder test")
  }
}
