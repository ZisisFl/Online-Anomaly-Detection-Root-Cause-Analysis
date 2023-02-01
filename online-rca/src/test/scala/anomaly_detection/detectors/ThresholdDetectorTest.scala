package anomaly_detection.detectors

import config.AppConfig
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.scalatest.flatspec.AnyFlatSpec

class ThresholdDetectorTest extends AnyFlatSpec{
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  AppConfig.enableCheckpoints(env)

  "test threshold detector" should "detect anomalies " in {
    var spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.metric = "ws_quantity"
    spec.timestamp = "sale_at"

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    detector.runDetection(env)

    env.execute()
  }
}
