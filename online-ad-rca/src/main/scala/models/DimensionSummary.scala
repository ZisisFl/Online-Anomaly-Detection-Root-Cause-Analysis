package models

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import utils.Types.MetricValue

case class DimensionSummary(
                           dimension: Dimension,
                           currentValue: MetricValue,
                           baselineValue: MetricValue,
                           cost: Double,
                           valueChangePercentage: Double,
                           contributionChangePercentage: Double,
                           contributionToOverallChangePercentage: Double
                         ) {

  override def toString: String = {
    "DimensionSummary(dimension=%s, currentValue=%s, baselineValue=%s, cost=%s, valueChangePercentage=%s, contributionChangePercentage=%s, contributionToOverallChangePercentage=%s)".format(
      dimension,
      currentValue,
      baselineValue,
      cost,
      valueChangePercentage,
      contributionChangePercentage,
      contributionToOverallChangePercentage
    )
  }

  def toObjectNode(objectMapper: ObjectMapper): ObjectNode = {
    val node: ObjectNode = objectMapper.createObjectNode()
    node.set("dimension", dimension.toObjectNode(objectMapper))
    node.put("currentValue", currentValue)
    node.put("baselineValue", baselineValue)
    node.put("cost", cost)
    node.put("valueChangePercentage", valueChangePercentage)
    node.put("contributionChangePercentage", contributionChangePercentage)
    node.put("contributionToOverallChangePercentage", contributionToOverallChangePercentage)

    node
  }
}
