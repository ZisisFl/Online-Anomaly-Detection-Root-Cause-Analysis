package jobs

import config.AppConfig
import models.{Dimension, InputRecord}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import sources.kafka.GenericJsonKafkaConsumer

object AnomalyDetectionJob {
  def main(args: Array[String]) {

    // Parse program arguments
    val parameters = ParameterTool.fromArgs(args)

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(parameters)
    AppConfig.enableCheckpoints(env)

    val fromTime = "earliest"

    val inputOrdersStream: DataStream[InputRecord]= {
      val kafkaConsumer = GenericJsonKafkaConsumer("test1", "flink_job")

      val kafkaConsumerWithOffset = fromTime.toLowerCase match {
        case "earliest" => kafkaConsumer.setStartFromEarliest()
        case "latest" => kafkaConsumer.setStartFromLatest()
        case t => kafkaConsumer.setStartFromTimestamp(t.toLong)
      }
      env.addSource(kafkaConsumerWithOffset)
        .setParallelism(1)
        .map({
          record => InputRecord(
            id = "yo",
            timestamp = record.get("sale_at").toString,
            value = record.get("ws_ext_list_price").toString.toDouble,
            dimensions = Map(
              "ca_city" -> record.get("ca_city").toString,
              "ca_country"-> record.get("ca_country").toString
            )
          )
        })
    }
    inputOrdersStream
      .print()

    env.execute("Anomaly Detection Job")
  }
}
