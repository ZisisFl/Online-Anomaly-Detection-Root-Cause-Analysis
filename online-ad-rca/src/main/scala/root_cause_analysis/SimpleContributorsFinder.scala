package root_cause_analysis

import models.{AnomalyEvent, Dimension, DimensionSummary, RCAResult}
import org.apache.flink.streaming.api.scala.{DataStream, createTypeInformation}
import utils.Types.MetricValue

class SimpleContributorsFinder extends ContributorsFinder {
  /**
   * Similar logic to computeStats method of Startree Thirdeye
   * thirdeye-plugins/thirdeye-contributors-simple/src/main/java/ai/startree/thirdeye/plugins/rca/contributors/simple/SimpleContributorsFinder.java
   * @param aggregatedRecordsWBaseline
   */

  override def runSearch(anomalyStream: DataStream[AnomalyEvent]): DataStream[RCAResult] = {
    anomalyStream
      .map(anomaly => search(anomaly))
  }

  def search(anomalyEvent: AnomalyEvent, dimensionGroup: String = "all"): RCAResult = {
    val currentTotal = anomalyEvent.aggregatedRecordsWBaseline.current
    val baselineTotal = anomalyEvent.aggregatedRecordsWBaseline.baseline

    RCAResult(
      anomalyEvent.anomalyId,
      anomalyEvent.detectedAt,
      currentTotal,
      baselineTotal,
      dimensionGroup,
      computeSummaries(
        currentTotal,
        baselineTotal,
        anomalyEvent.aggregatedRecordsWBaseline.current_dimensions_breakdown,
        anomalyEvent.aggregatedRecordsWBaseline.baseline_dimensions_breakdown
      )
    )
  }

  private def computeSummaries(
                    currentTotal: Double,
                    baselineTotal: Double,
                    currentDimensionsBreakdown: Map[Dimension, MetricValue],
                    baselineDimensionsBreakdown: Map[Dimension, MetricValue]
                  ): List[DimensionSummary] = {

    // some Dimensions(name, value) tuples are not present in both tables - fill those with zeroes
    (currentDimensionsBreakdown.keySet ++ baselineDimensionsBreakdown.keySet).map(dim => {
      val currentValue: Double = currentDimensionsBreakdown.getOrElse(dim, 0)
      val baselineValue: Double = baselineDimensionsBreakdown.getOrElse(dim, 0)

      val stats = new Stats(baselineValue, currentValue, baselineTotal, currentTotal)

      val cost = SimpleContributorsCost.compute(
        stats.valueChangePercentage,
        stats.contributionChangePercentage,
        stats.contributionToOverallChangePercentage
      )

      DimensionSummary(
        dim,
        currentValue,
        baselineValue,
        cost,
        stats.valueChangePercentage,
        stats.contributionChangePercentage,
        stats.contributionToOverallChangePercentage
      )
    }).toList
      .filter(_.cost > 0) // filter out DimensionStats objects with cost <= 0
      .sortBy(-_.cost) // sort resulting list of DimensionStats by descending cost
  }
}