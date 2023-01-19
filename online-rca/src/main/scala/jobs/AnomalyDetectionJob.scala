package jobs

import config.AppConfig
import models.{Dimension, InputRecord}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import sources.kafka.{GenericJsonKafkaConsumer, InputRecordStreamBuilder}

object AnomalyDetectionJob {
  def main(args: Array[String]) {

    // Parse program arguments
    val parameters = ParameterTool.fromArgs(args)

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(parameters)
    AppConfig.enableCheckpoints(env)

    val inputStream: DataStream[InputRecord] = InputRecordStreamBuilder.buildInputRecordStream(
      "test1",
      env,
      1)
    inputStream
      .print()

    env.execute("Anomaly Detection Job")
  }
}
