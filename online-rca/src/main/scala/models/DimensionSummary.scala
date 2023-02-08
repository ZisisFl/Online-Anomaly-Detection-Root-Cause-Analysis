package models

import utils.Types.MetricValue

case class DimensionSummary(
                           dimension: Dimension,
                           currentValue: MetricValue,
                           baselineValue: MetricValue,
                           cost: Double,
                           valueChangePercentage: Double,
                           contributionChangePercentage: Double,
                           contributionToOverallChangePercentage: Double
                         ) {

  override def toString: String = {
    "DimensionStats(dimension=%s, currentValue=%s, baselineValue=%s, cost=%s, valueChangePercentage=%s, contributionChangePercentage=%s, contributionToOverallChangePercentage=%s)".format(
      dimension,
      currentValue,
      baselineValue,
      cost,
      valueChangePercentage,
      contributionChangePercentage,
      contributionToOverallChangePercentage
    )
  }
}
