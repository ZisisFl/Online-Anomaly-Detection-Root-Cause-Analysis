package root_cause_analysis

import models.{AggregatedRecordsWBaseline, Dimension, DimensionStats, RCAResult}
import utils.Types.{ChildDimension, MetricValue, ParentDimension}

/**
 * According to thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/cost/BalancedCostFunction.java
 *
 * Returns the cost that consider change difference, change changeRatio, and node size (contribution percentage of a node).
 */
class HierarchicalContributorsFinder extends Serializable {

  private final val MINIMUM_CONTRIBUTION_OF_INTEREST_PERCENTAGE = 3d

  def search(aggregatedRecordsWBaseline: AggregatedRecordsWBaseline): RCAResult = {
    val currentTotal = aggregatedRecordsWBaseline.current
    val baselineTotal = aggregatedRecordsWBaseline.baseline

    RCAResult(
      currentTotal,
      baselineTotal,
      computeStats(
        currentTotal,
        baselineTotal,
        aggregatedRecordsWBaseline.current_dimensions_breakdown,
        aggregatedRecordsWBaseline.baseline_dimensions_breakdown,
        aggregatedRecordsWBaseline.dimensions_hierarchy
      )
    )
  }

  def computeStats(
                    currentTotal: Double,
                    baselineTotal: Double,
                    currentDimensionsBreakdown: Map[Dimension, MetricValue],
                    baselineDimensionsBreakdown: Map[Dimension, MetricValue],
                    dimensionsHierarchy: Map[ChildDimension, ParentDimension]
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

      /**
       * According to the implementation of AdditiveCubeNode which is used to represent a node in the
       * hierarchy graph for an additive metric get{Baseline|Current}Size() methods return {Baseline|Current}Value
       * Implementation of AdditiveCubeNode in the original ThirdEye project
       * thirdeye-pinot/src/main/java/org/apache/pinot/thirdeye/cube/additive/AdditiveCubeNode.java
       * Other than AdditiveCubeNode there is also the implementation of RatioCubeNode for ratio metrics
       */
      val baselineSize = baselineValue
      val currentSize = currentValue
      val baselineTotalSize = baselineTotal
      val currentTotalSize = currentTotal
      val parentCurrentValue = getParentValue(dim, dimensionsHierarchy, currentDimensionsBreakdown, currentTotal)
      val parentBaselineValue = getParentValue(dim, dimensionsHierarchy, baselineDimensionsBreakdown, baselineTotal)

      val parentRatio = Stats.computeChangeRatio(parentBaselineValue, parentCurrentValue)

      val contribution = Stats.computeContribution(baselineSize, currentSize, baselineTotalSize, currentTotalSize)

      DimensionStats(
        dim,
        currentValue,
        baselineTotal,
        HierarchicalContributorsCost.compute(baselineValue, currentValue, parentRatio, contribution)
      )
    }).toList
      .filter(_.cost > 0) // filter out DimensionStats objects with cost <= 0
      .sortBy(-_.cost) // sort resulting list of DimensionStats by descending cost
  }

  def getParentValue(
                 childDim: Dimension,
                 dimensionsHierarchy: Map[ChildDimension, ParentDimension],
                 dimensionsBreakdown: Map[Dimension, MetricValue],
                 valueTotal: Double): Double = {
    /**
     * We could add an object Dimension(root, {current|baseline}ValueTotal) in the dimensionsBreakdown
     * and not filter out root parent in DimensionHierarchy in order to handle here uniformly parent dimension handling.
     * This solution would also require Aggregators' getResult method to add the Dimension(root, {current|baseline}ValueTotal)
     *
     * But in the way that we handle it we save up space as we do not store pairs of Map[ChildDimension, ParentDimension]
     * when parent is root.
     *
     */

    // find parent
    val parentDim = dimensionsHierarchy.getOrElse(childDim, Dimension("root", "none"))

    // get MetricValue of parent
    // if Dimension doesn't exist in dimensionsHierarchy means parentDim is root so set value to valueTotal
    // otherwise search for value in dimensionsBreakdown which may be absent as happens for current calculation
    val parentValue: Double = parentDim.name match {
      case "root" => valueTotal
      case _ => dimensionsBreakdown.getOrElse(parentDim, 0d)
    }

    parentValue
    }
}