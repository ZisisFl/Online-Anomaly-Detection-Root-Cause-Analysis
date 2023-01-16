package utils

import models.Dimension

package object Types {
  type NumericalDimension = Dimension[Int]
  type CategoricalDimension = Dimension[String]
  type DimensionName = String
}
