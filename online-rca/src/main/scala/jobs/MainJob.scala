package jobs

import anomaly_detection.detectors.{ThresholdDetector, ThresholdDetectorSpec}
import config.AppConfig
import models.{AnomalyEvent, Dimension, InputRecord}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import root_cause_analysis.HierarchicalContributorsFinder
import sources.kafka.InputRecordStreamBuilder

object MainJob {
  def main(args: Array[String]) {

    // Parse program arguments
//    val parameters = ParameterTool.fromArgs(args)

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
//    env.getConfig.setGlobalJobParameters(parameters)
    AppConfig.enableCheckpoints(env)

    // load input stream
    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1)

    // set up anomaly detector
    val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

    spec.min = 3000.0f
    spec.max = 5000.0f
    spec.metric = "ws_quantity"
    spec.timestamp = "sale_at"

    val detector: ThresholdDetector = new ThresholdDetector()
    detector.init(spec)

    val anomaliesStream: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    // apply contributors finder
    val hierarchicalContributorsFinder = new HierarchicalContributorsFinder()

    val rcaStream = anomaliesStream.map(anomaly => hierarchicalContributorsFinder.search(anomaly))

    rcaStream.print()

    env.execute("Anomaly Detection Job")
  }
}
