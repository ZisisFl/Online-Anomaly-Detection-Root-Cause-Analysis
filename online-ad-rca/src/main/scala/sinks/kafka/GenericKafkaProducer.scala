package sinks.kafka

import config.AppConfig
import models.RCAResult
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import serialization.RCAResultSerializationSchema

import java.util.Properties

case class GenericKafkaProducer(topic: String) extends FlinkKafkaProducer[RCAResult](
  topic,
  new RCAResultSerializationSchema(topic),
  {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", AppConfig.Kafka.BOOTSTRAP_SERVERS)

    properties
  },
  FlinkKafkaProducer.Semantic.EXACTLY_ONCE
)
//https://github.com/apache/flink/blob/db4f7781173bdc5790f7b3e1fd3eeebfa4b31fc9/flink-formats/flink-json/src/main/java/org/apache/flink/formats/json/JsonSerializationSchema.java