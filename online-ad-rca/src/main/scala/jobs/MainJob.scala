package jobs

import anomaly_detection.detectors.{ThresholdDetector, ThresholdDetectorSpec}
import config.AppConfig
import models.{AnomalyEvent, Dimension, InputRecord}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import root_cause_analysis.{HierarchicalContributorsFinder, SimpleContributorsFinder}
import sources.kafka.InputRecordStreamBuilder

object MainJob {
  def main(args: Array[String]) {

//    // Parse program arguments
//    val parameters = ParameterTool.fromArgs(args)
//    env.getConfig.setGlobalJobParameters(parameters)

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    AppConfig.enableCheckpoints(env)

    // load input stream
    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1)

    val spec = {
      if (AppConfig.AnomalyDetection.METHOD == "threshold") {
        val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

        spec.min = 3000.0f
        spec.max = 5000.0f

        spec
      }
      else {
        // ThresholdDetector is the default
        val spec: ThresholdDetectorSpec = new ThresholdDetectorSpec()

        spec.min = 3000.0f
        spec.max = 5000.0f

        spec
      }
    }

    val detector = {
      if (AppConfig.AnomalyDetection.METHOD == "threshold") {
        val detector: ThresholdDetector = new ThresholdDetector()
        detector.init(spec)

        detector
      }
      else {
        // ThresholdDetector is the default
        val detector: ThresholdDetector = new ThresholdDetector()
        detector.init(spec)

        detector
      }
    }

    val anomaliesStream: DataStream[AnomalyEvent] = detector.runDetection(inputStream)

    // apply contributors finder
    val contributorsFinder = {
      if (AppConfig.RootCauseAnalysis.METHOD == "hierarchical") {
        new HierarchicalContributorsFinder().runSearch(anomaliesStream)
      }
      else if (AppConfig.RootCauseAnalysis.METHOD == "simple") {
        new SimpleContributorsFinder().runSearch(anomaliesStream)
      }
    }

    env.execute("Anomaly Detection Job")
  }
}
