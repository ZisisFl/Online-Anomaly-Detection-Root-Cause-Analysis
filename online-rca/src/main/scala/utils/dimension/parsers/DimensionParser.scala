package utils.dimension.parsers

import models.Dimension

abstract class DimensionParser[T] {
  def parseValue(name: String, value: T): Dimension
}