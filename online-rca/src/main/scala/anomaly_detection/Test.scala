package anomaly_detection

import config.AppConfig
import models.SaleRecord
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.{SimpleStringSchema}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.kafka.common.serialization.StringDeserializer
import sources.kafka.KafkaConsumer
import utils.deserializers.JsonDeserializer

object Test {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val source: KafkaSource[String] = KafkaSource.builder()
      .setBootstrapServers(AppConfig.Kafka.BOOTSTRAP_SERVERS)
      .setTopics("web_sales")
      .setGroupId(AppConfig.Kafka.GROUP_ID)
      .setStartingOffsets(OffsetsInitializer.earliest())
      .setValueOnlyDeserializer(new SimpleStringSchema())
      .build()

//    val sales: DataStream[SaleRecord] = {
//      val kafkaConsumer = KafkaConsumer("web_sales", "test-job")
//
//      env.addSource(kafkaConsumer)
//    }
    val sales: DataStream[String] = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Yo")

    sales.print()

    env.execute()
  }
}
