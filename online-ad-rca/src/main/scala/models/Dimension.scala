package models

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import utils.Types.{DimensionGroup, DimensionName, DimensionValue}

case class Dimension(name: DimensionName, value: DimensionValue, group: DimensionGroup) {

  override def toString = {
    "Dimension(name=%s, value=%s, group=%s)".format(name, value, group)
  }

  def toObjectNode(objectMapper: ObjectMapper): ObjectNode = {
    val node: ObjectNode = objectMapper.createObjectNode()
    node.put("name", name)
    node.put("value", value)
    node.put("group", group)

    node
  }
}