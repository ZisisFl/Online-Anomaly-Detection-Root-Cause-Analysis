package models

import utils.Types.MetricValue

case class DimensionStats(
                           dimension: Dimension,
                           currentValue: MetricValue,
                           baselineValue: MetricValue,
                           valueChangePercentage: Double,
                           contributionChangePercentage: Double,
                           contributionToOverallChangePercentage: Double,
                           cost: Double
                         ) {

  override def toString: String = {
    "DimensionStats(dimension=%s, currentValue=%s, baselineValue=%s, valueChangePercentage=%s, contributionChangePercentage=%s, contributionToOverallChangePercentage=%s, cost=%s)".format(
      dimension,
      currentValue,
      baselineValue,
      valueChangePercentage,
      contributionChangePercentage,
      contributionToOverallChangePercentage,
      cost
    )
  }
}
