package anomaly_detection

import models.{Dimension, InputRecord}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import sources.kafka.KafkaConsumer

object MainJob {
  def main(args: Array[String]) {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val fromTime = "earliest"

    val inputOrdersStream: DataStream[InputRecord]= {
      val kafkaConsumer = KafkaConsumer("test1", "flink_job")

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
            dimensions = List(
              Dimension(name = "ca_city", value = record.get("ca_city").toString),
              Dimension(name = "ca_country", value = record.get("ca_country").toString)
            )
          )
        })
    }
    inputOrdersStream
      .print()

    env.execute("Main Job")
  }
}
