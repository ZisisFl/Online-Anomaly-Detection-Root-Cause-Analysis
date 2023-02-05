package root_cause_analysis.complex

import models.{AggregatedRecordsWBaseline, Dimension, DimensionStats, RCAResult}
import utils.Types.MetricValue

/**
 * According to thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
 *
 * Returns the cost that consider change difference, change changeRatio, and node size (contribution percentage of a node).
 */
class ContributorsFinder extends Serializable {
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
                  ): Unit = {

    // some Dimensions(name, value) tuples are not present in both tables - fill those with zeroes
    val yo = (currentDimensionsBreakdown.keySet ++ baselineDimensionsBreakdown.keySet).map(dim => {
      val currentValue: Double = currentDimensionsBreakdown.getOrElse(dim, 0)
      val baselineValue: Double = baselineDimensionsBreakdown.getOrElse(dim, 0)
    })
  }
}
