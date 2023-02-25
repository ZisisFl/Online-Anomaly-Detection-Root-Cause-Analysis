package utils.dimension.parsers

import models.Dimension

class BooleanDimensionParser extends DimensionParser[Boolean] {
  def parseValue(name: String, value: Boolean, group: String, level: Int): Dimension = {
    Dimension(name, value.toString, group, level)
  }
}
