package sources.kafka

import config.AppConfig
import models.InputRecord
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}

object InputRecordStream {

  def createInputRecordStream(
                        kafkaTopic: String,
                        kafkaOffset: String,
                        env: StreamExecutionEnvironment,
                        parallelism: Int,
                        groupId: String = AppConfig.Kafka.GROUP_ID): DataStream[InputRecord] = {

    val inputOrdersStream: DataStream[InputRecord] = {
      val kafkaConsumer = GenericJsonKafkaConsumer(kafkaTopic, groupId)

      val kafkaConsumerWithOffset = kafkaOffset.toLowerCase match {
        case "earliest" => kafkaConsumer.setStartFromEarliest()
        case "latest" => kafkaConsumer.setStartFromLatest()
        case t => kafkaConsumer.setStartFromTimestamp(t.toLong)
      }
      env.addSource(kafkaConsumerWithOffset)
        .setParallelism(parallelism)
        .map(record =>
          InputRecord(
            timestamp = record.get("value").get("sale_at").textValue(),
            value = record.get("value").get("ws_ext_list_price").doubleValue(),
            dimensions = Map(
              "ca_city" -> record.get("value").get("ca_city").textValue(),
              "ca_country" -> record.get("value").get("ca_country").textValue()
            )
          )
        )
    }
    inputOrdersStream
  }
}
