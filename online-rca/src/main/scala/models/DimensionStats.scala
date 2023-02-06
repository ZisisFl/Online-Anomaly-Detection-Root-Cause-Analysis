package models

import utils.Types.MetricValue

case class DimensionStats(
                           dimension: Dimension,
                           currentValue: MetricValue,
                           baselineValue: MetricValue,
                           cost: Double
                         ) {

  override def toString: String = {
    "DimensionStats(dimension=%s, currentValue=%s, baselineValue=%s, cost=%s)".format(
      dimension,
      currentValue,
      baselineValue,
      cost
    )
  }
}
