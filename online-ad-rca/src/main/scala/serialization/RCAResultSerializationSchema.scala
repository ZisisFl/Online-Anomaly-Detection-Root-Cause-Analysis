package serialization

import models.RCAResult
import org.apache.flink.api.common.serialization.SerializationSchema
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper

class RCAResultSerializationSchema extends SerializationSchema[RCAResult] {
  private val objectMapper = new ObjectMapper()

  override def serialize(element: RCAResult): Array[Byte] = {
    objectMapper.writeValueAsString(element.toObjectNode(objectMapper)).getBytes("UTF-8")
  }
}
