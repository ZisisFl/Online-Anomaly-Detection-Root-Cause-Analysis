package utils.dimension

import config.AppConfig.InputStream.{DIMENSION_DEFINITIONS, DIMENSION_NAMES}
import models.Dimension
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import utils.Types.DimensionName
import utils.dimension.parsers.{BooleanDimensionParser, IntDimensionParser, StringDimensionParser}

object DimensionsBuilder {
  private def buildDimension(dimName: String, record: ObjectNode): Dimension = {
    val dimensionConfig = DIMENSION_DEFINITIONS.getConfig(dimName)
    val dimensionValueType = dimensionConfig.getString("value_type")
    val dimensionGroup = dimensionConfig.getString("group")

    dimensionValueType match {
      case "string" => new StringDimensionParser().parseValue(
        name = dimName,
        value = record.get("value").get(dimName).textValue(),
        group = dimensionGroup
      )
      case "int" => new IntDimensionParser().parseValue(
        name = dimName,
        value = record.get("value").get(dimName).intValue(),
        group = dimensionGroup
      )
      case "bool" => new BooleanDimensionParser().parseValue(
        name = dimName,
        value = record.get("value").get(dimName).booleanValue(),
        group = dimensionGroup
      )
    }
  }

  def buildDimensionsMap(record: ObjectNode): Map[DimensionName, Dimension] = {
    DIMENSION_NAMES.map {
      dimName => {
        dimName -> buildDimension(dimName, record)
      }
    }.toMap
  }
}