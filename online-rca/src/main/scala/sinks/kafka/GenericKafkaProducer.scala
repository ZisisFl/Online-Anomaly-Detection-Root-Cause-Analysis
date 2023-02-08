package sinks.kafka

import config.AppConfig
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer

case class GenericKafkaProducer(topic: String) extends FlinkKafkaProducer[String](AppConfig.Kafka.BOOTSTRAP_SERVERS, topic, new SimpleStringSchema())
//https://github.com/apache/flink/blob/db4f7781173bdc5790f7b3e1fd3eeebfa4b31fc9/flink-formats/flink-json/src/main/java/org/apache/flink/formats/json/JsonSerializationSchema.java