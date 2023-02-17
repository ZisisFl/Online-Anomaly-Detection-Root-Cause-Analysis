package serialization

import models.RCAResult
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

import java.lang

class RCAResultSerializationSchema(topic: String) extends KafkaSerializationSchema[RCAResult] {
  private val objectMapper = new ObjectMapper()

  override def serialize(element: RCAResult, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val node: ObjectNode = objectMapper.createObjectNode()
    node.put("relatedAnomalyId", element.relatedAnomalyId)
    node.put("detectedAt", element.detectedAt.toString)
    node.put("currentTotal", element.currentTotal)
    node.put("baselineTotal", element.baselineTotal)
//    node.putArray("dimensionSummaries").addAll(
//      element.dimensionSummaries.map { summary =>
//        val summaryNode = mapper.createObjectNode()
//        summaryNode.put("dimension", summary.dimension)
//        summaryNode.put("currentValue", summary.currentValue)
//        summaryNode.put("baselineValue", summary.baselineValue)
//        summaryNode.put("percentDifference", summary.percentDifference)
//        summaryNode
//      }
//    )
//    node.put()

    val serializedValue = objectMapper.writeValueAsString(node).getBytes("UTF-8")
    new ProducerRecord[Array[Byte], Array[Byte]](topic, serializedValue)
  }
}
