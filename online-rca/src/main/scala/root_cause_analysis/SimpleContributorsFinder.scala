package root_cause_analysis

import models.{AggregatedRecordsWBaseline, Dimension, DimensionStats, RCAResult}
import utils.Types.MetricValue

class SimpleContributorsFinder extends Serializable {
  /**
   * Similar logic to computeStats method of
   * thirdeye-plugins/thirdeye-contributors-simple/src/main/java/ai/startree/thirdeye/plugins/rca/contributors/simple/SimpleContributorsFinder.java
   * @param baseline_dimensions_breakdown
   */

  def search(aggregatedRecordsWBaseline: AggregatedRecordsWBaseline): RCAResult = {
    val currentTotal = aggregatedRecordsWBaseline.current
    val baselineTotal = aggregatedRecordsWBaseline.baseline

    RCAResult(
      aggregatedRecordsWBaseline.current,
      aggregatedRecordsWBaseline.baseline,
      computeStats(
        currentTotal,
        baselineTotal,
        aggregatedRecordsWBaseline.current_dimensions_breakdown,
        aggregatedRecordsWBaseline.baseline_dimensions_breakdown
      )
    )
  }

  private def computeStats(
                    currentTotal: Double,
                    baselineTotal: Double,
                    currentDimensionsBreakdown: Map[Dimension, MetricValue],
                    baselineDimensionsBreakdown: Map[Dimension, MetricValue]
                  ): List[DimensionStats] = {

    // some Dimensions(name, value) tuples are not present in both tables - fill those with zeroes
    (currentDimensionsBreakdown.keySet ++ baselineDimensionsBreakdown.keySet).map(dim => {
      val currentValue: Double = currentDimensionsBreakdown.getOrElse(dim, 0)
      val baselineValue: Double = baselineDimensionsBreakdown.getOrElse(dim, 0)

      val valueChangePercentage = Stats.computeValueChangePercentage(
        baselineValue,
        currentValue
      )

      val contributionChangePercentage = Stats.computeContributionChangePercentage(
        baselineValue,
        currentValue,
        baselineTotal,
        currentTotal
      )

      val contributionToOverallChangePercentage = Stats.computeContributionToOverallChangePercentage(
        baselineValue,
        currentValue,
        baselineTotal,
        currentTotal
      )

      DimensionStats(
        dim,
        currentValue,
        baselineTotal,
        valueChangePercentage,
        contributionChangePercentage,
        contributionToOverallChangePercentage,
        Cost.compute(valueChangePercentage, contributionChangePercentage, contributionToOverallChangePercentage)
      )
    }).toList.sortBy(-_.cost)
  }
}