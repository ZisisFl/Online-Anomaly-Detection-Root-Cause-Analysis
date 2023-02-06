package anomaly_detection.detectors

import config.AppConfig
import models.AggregatedRecordsWBaseline
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.scalatest.flatspec.AnyFlatSpec

class ThresholdDetectorTest extends AnyFlatSpec{
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "test threshold detector" should "detect anomalies " in {
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.metric = "ws_quantity"
    spec.timestamp = "sale_at"

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val output: DataStream[AggregatedRecordsWBaseline] = detector.runDetection(env)

    output.print()

    env.execute()
  }
}
