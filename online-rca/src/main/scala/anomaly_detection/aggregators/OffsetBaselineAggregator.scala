package anomaly_detection.aggregators

import models.{AggregatedRecords, AggregatedRecordsWBaseline, Dimension}
import org.apache.flink.api.common.functions.AggregateFunction
import utils.Types.MetricValue

/**
 * Creates records containing baseline and current values and dimensions
 */
class OffsetBaselineAggregator extends AggregateFunction[AggregatedRecords, Seq[AggregatedRecords], AggregatedRecordsWBaseline]{
  override def createAccumulator(): Seq[AggregatedRecords] = Seq[AggregatedRecords]()

  override def add(value: AggregatedRecords, accumulator: Seq[AggregatedRecords]): Seq[AggregatedRecords] = {
    accumulator :+ value
  }

  /**
   * Perform averaging to get offset baseline values
   * @param accumulator
   */
  override def getResult(accumulator: Seq[AggregatedRecords]): AggregatedRecordsWBaseline = {
    val result = findLast(accumulator)

    val last = result._1
    val rest = result._2

    if (rest.isEmpty) {
      AggregatedRecordsWBaseline(
        current = last.current,
        baseline = Double.NaN,
        current_dimensions_breakdown = last.dimensions_breakdown,
        baseline_dimensions_breakdown = Map[Dimension, MetricValue](),
        dimensions_hierarchy = last.dimensions_hierarchy,
        records_in_baseline_offset = 0)
    }
    else {
      AggregatedRecordsWBaseline(
        current = last.current,
        baseline = rest.map(_.current).sum / rest.length,
        current_dimensions_breakdown = last.dimensions_breakdown,
        baseline_dimensions_breakdown = rest.map(_.dimensions_breakdown).reduceLeft(_++_).groupBy(_._1).mapValues(x => x.values.sum / rest.length),
        dimensions_hierarchy = rest.map(_.dimensions_hierarchy).reduceLeft(_++_),
        records_in_baseline_offset = rest.length
      )
    }
  }

  override def merge(a: Seq[AggregatedRecords], b: Seq[AggregatedRecords]): Seq[AggregatedRecords] = {
    a ++ b
  }

  /**
   * Finds the last record in the accumulator according to window_starting_epoch
   * @param accumulator
   * @return The latest record and the accumulator without the latest record
   */
  private def findLast(accumulator: Seq[AggregatedRecords]): (AggregatedRecords, Seq[AggregatedRecords]) = {
    val last: AggregatedRecords = accumulator.maxBy(_.window_starting_epoch)
    val rest: Seq[AggregatedRecords] = accumulator.filter(_ != last)

    (last, rest)
  }
}
