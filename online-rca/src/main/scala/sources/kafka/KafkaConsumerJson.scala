package sources.kafka

import config.AppConfig
import models.SaleRecord
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import utils.deserializers.JsonDeserializer

class KafkaConsumerJson {
  val source: KafkaSource[SaleRecord] = KafkaSource.builder()
    .setBootstrapServers(AppConfig.Kafka.BOOTSTRAP_SERVERS)
    .setTopics("web_sales")
    .setGroupId(AppConfig.Kafka.GROUP_ID)
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new JsonDeserializer())
    .build()
}
