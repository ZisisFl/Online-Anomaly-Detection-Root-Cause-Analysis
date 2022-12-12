package sources.kafka

import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import config.AppConfig
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode

import java.util.Properties


case class KafkaConsumer(topicName: String, groupId: String = AppConfig.Kafka.GROUP_ID) extends FlinkKafkaConsumer[ObjectNode](
  topicName,
  new JSONKeyValueDeserializationSchema(false),
  {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", AppConfig.Kafka.BOOTSTRAP_SERVERS)
    properties.setProperty("group.id", groupId)

    properties
  }
)
