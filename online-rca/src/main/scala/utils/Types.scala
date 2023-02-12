package utils

import models.Dimension

package object Types {
  type DimensionName = String
  type DimensionValue = String
  type DimensionGroup = String
  type MetricValue = Double
  type ChildDimension = Dimension
  type ParentDimension = Dimension
}
