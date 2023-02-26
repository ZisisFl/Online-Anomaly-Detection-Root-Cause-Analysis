package sources.kafka

import config.AppConfig
import models.InputRecord
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import utils.DimensionHierarchiesBuilder.buildHierarchies
import utils.dimension.DimensionsBuilder

object InputRecordStreamBuilder {
  def buildInputRecordStream(
                               env: StreamExecutionEnvironment,
                               kafkaTopic: String = AppConfig.InputStream.INPUT_TOPIC,
                               kafkaOffset: String = "earliest",
                               groupId: String = AppConfig.Kafka.GROUP_ID): DataStream[InputRecord] = {

    val inputOrdersStream: DataStream[InputRecord] = {
      val kafkaConsumer = GenericJsonConsumer(kafkaTopic, groupId)

      val kafkaConsumerWithOffset = kafkaOffset.toLowerCase match {
        case "earliest" => kafkaConsumer.setStartFromEarliest()
        case "latest" => kafkaConsumer.setStartFromLatest()
        case t => kafkaConsumer.setStartFromTimestamp(t.toLong)
        //case _ => throw new IllegalArgumentException("kafkaOffset can either be earliest, latest or a timestamp")
      }
      env.addSource(kafkaConsumerWithOffset)
        .setParallelism(env.getParallelism - 1)
        .map(record => buildInputRecord(record))
    }
    inputOrdersStream
  }

  private def buildInputRecord(record: ObjectNode): InputRecord = {
    val dimensions = DimensionsBuilder.buildDimensionsMap(record)
    InputRecord(
      timestamp = record.get("value").get(AppConfig.InputStream.TIMESTAMP_FIELD).textValue(),
      value = record.get("value").get(AppConfig.InputStream.VALUE_FIELD).doubleValue(),
      dimensions = dimensions,
      dimensions_hierarchy = buildHierarchies(dimensions)
    )
  }
}
