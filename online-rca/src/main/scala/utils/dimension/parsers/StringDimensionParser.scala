package utils.dimension.parsers

import models.Dimension

class StringDimensionParser extends DimensionParser[String] {
  def parseValue(name: String, value: String): Dimension = {
    Dimension(name, value)
  }
}
