package root_cause_analysis

import models.{AggregatedRecordsWBaseline, Dimension, DimensionStats}
import utils.Types.MetricValue

/**
 * According to thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
 *
 * Returns the cost that consider change difference, change changeRatio, and node size (contribution percentage of a node).
 */
class HierarchicalContributorsFinder extends Serializable {

  private final val MINIMUM_CONTRIBUTION_OF_INTEREST_PERCENTAGE = 3d

  def search(aggregatedRecordsWBaseline: AggregatedRecordsWBaseline): Unit = {
    val currentTotal = aggregatedRecordsWBaseline.current
    val baselineTotal = aggregatedRecordsWBaseline.baseline

//    RCAResult(currentTotal, baselineTotal)
  }

  def computeStats(
                    currentTotal: Double,
                    baselineTotal: Double,
                    currentDimensionsBreakdown: Map[Dimension, MetricValue],
                    baselineDimensionsBreakdown: Map[Dimension, MetricValue]
                  ): List[DimensionStats] = {

    // some Dimensions(name, value) tuples are not present in both tables - fill those with zeroes
    (currentDimensionsBreakdown.keySet ++ baselineDimensionsBreakdown.keySet).map(dim => {
      val currentValue: Double = currentDimensionsBreakdown.getOrElse(dim, 0)
      val baselineValue: Double = baselineDimensionsBreakdown.getOrElse(dim, 0)

      val contributionToOverallChangePercentage = Stats.computeContributionToOverallChangePercentage(
        baselineValue,
        currentValue,
        baselineTotal,
        currentTotal
      )

      // Typically, users don't care nodes with small contribution to overall changes
      if (contributionToOverallChangePercentage < MINIMUM_CONTRIBUTION_OF_INTEREST_PERCENTAGE) 0d

      val parentRatio = 0d
      val baselineSize = 0d
      val currentSize = 0d
      val baselineTotalSize = 0d
      val currentTotalSize = 0d

      val contribution = Stats.computeContribution(baselineSize, currentSize, baselineTotalSize, currentTotalSize)

      DimensionStats(
        dim,
        currentValue,
        baselineTotal,
        HierarchicalContributorsCost.compute(baselineValue, currentValue, parentRatio, contribution)
      )
    }).toList
  }
}
