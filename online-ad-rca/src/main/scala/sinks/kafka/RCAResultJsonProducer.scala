package sinks.kafka

import config.AppConfig
import models.RCAResult
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import serialization.RCAResultSerializationSchema

import java.util.Properties

case class RCAResultJsonProducer(topic: String = AppConfig.RootCauseAnalysis.OUTPUT_TOPIC) extends FlinkKafkaProducer[RCAResult](
  topic,
  new RCAResultSerializationSchema,
  {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", AppConfig.Kafka.BOOTSTRAP_SERVERS)

    properties
  }
)
// https://github.com/mkuthan/example-flink-kafka/blob/master/src/main/scala/example/flink/FlinkExample.scala