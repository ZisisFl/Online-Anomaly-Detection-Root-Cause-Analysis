package utils.dimension.parsers

import models.Dimension

class IntDimensionParser extends DimensionParser[Int] {
  def parseValue(name: String, value: Int): Dimension = {
    Dimension(name, value.toString)
  }
}
