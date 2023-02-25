package utils.dimension.parsers

import models.Dimension

abstract class DimensionParser[T] {
  def parseValue(name: String, value: T, group: String, level: Int): Dimension
}