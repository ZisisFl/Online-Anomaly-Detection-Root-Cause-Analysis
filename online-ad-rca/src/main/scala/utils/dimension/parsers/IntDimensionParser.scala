package utils.dimension.parsers

import models.Dimension

class IntDimensionParser extends DimensionParser[Int] {
  def parseValue(name: String, value: Int, group: String): Dimension = {
    Dimension(name, value.toString, group)
  }
}
