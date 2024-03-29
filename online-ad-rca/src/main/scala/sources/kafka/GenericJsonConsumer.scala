package sources.kafka

import config.AppConfig
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import serialization.JSONDeserializationSchema

import java.util.Properties

case class GenericJsonConsumer(topicName: String, groupId: String = AppConfig.Kafka.GROUP_ID) extends FlinkKafkaConsumer[ObjectNode](
  topicName,
  new JSONKeyValueDeserializationSchema(false),
  {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", AppConfig.Kafka.BOOTSTRAP_SERVERS)
    properties.setProperty("group.id", groupId)

    properties
  }
)
