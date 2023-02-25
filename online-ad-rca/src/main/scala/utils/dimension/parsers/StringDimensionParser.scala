package utils.dimension.parsers

import models.Dimension

class StringDimensionParser extends DimensionParser[String] {
  def parseValue(name: String, value: String, group: String, level: Int): Dimension = {
    Dimension(name, value, group, level)
  }
}
