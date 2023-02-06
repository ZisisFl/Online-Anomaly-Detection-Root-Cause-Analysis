package root_cause_analysis

import anomaly_detection.detectors.{ThresholdDetector, ThresholdDetectorSpec}
import config.AppConfig
import models.AggregatedRecordsWBaseline
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HierarchicalContributorsFinderTest extends AnyFlatSpec with Matchers {
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "anomaly detection and rca" should "work" in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.metric = "ws_quantity"
    spec.timestamp = "sale_at"

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val output: DataStream[AggregatedRecordsWBaseline] = detector.runDetection(env)

    val simpleContributorsFinder = new SimpleContributorsFinder()


    output
      .map(anomaly => simpleContributorsFinder.search(anomaly))
      .print()

    env.execute()
  }

  "errorWithEmptyBaseline function" should "result to infinity" in {
    val currentValue = 5d
    val parentRatio = 0d

    val cost = HierarchicalContributorsCost.errorWithEmptyBaseline(currentValue, parentRatio)
    print(cost)
  }

  "errorWithEmptyBaseline function" should "result to 0" in {
    val currentValue = 5d
    val parentRatio = 1d

    val cost = HierarchicalContributorsCost.errorWithEmptyBaseline(currentValue, parentRatio)
    print(cost)
  }
}
