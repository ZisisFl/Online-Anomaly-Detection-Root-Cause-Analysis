package anomaly_detection.aggregators

import models.accumulators.OffsetBaselineAccumulator
import models.{AggregatedRecords, AggregatedRecordsWBaseline, Dimension}
import org.apache.flink.api.common.functions.AggregateFunction
import utils.Types.{ChildDimension, MetricValue, ParentDimension}

/**
 * Creates records containing baseline and current values and dimensions
 */
@Deprecated
class OffsetBaselineAggregatorOld extends AggregateFunction[AggregatedRecords, OffsetBaselineAccumulator, AggregatedRecordsWBaseline]{
  override def createAccumulator(): OffsetBaselineAccumulator = OffsetBaselineAccumulator(
    0,
    Map[Dimension, MetricValue](),
    0,
    Seq[(Dimension, Double)](),
    Map[ChildDimension, ParentDimension](),
    0
  )

  override def add(value: AggregatedRecords, accumulator: OffsetBaselineAccumulator): OffsetBaselineAccumulator = {
    // if current record has no dimensions this could be a problem
    if (accumulator.current_dimensions_breakdown.isEmpty) {
      OffsetBaselineAccumulator(
        value.current,
        value.dimensions_breakdown,
        accumulator.baseline,
        accumulator.baseline_dimensions,
        accumulator.dimensions_hierarchy ++ value.dimensions_hierarchy,
        accumulator.records_in_baseline_offset
      )
    }
    else {
      OffsetBaselineAccumulator(
        accumulator.current,
        accumulator.current_dimensions_breakdown,
        accumulator.baseline + value.current,
        accumulator.baseline_dimensions ++ value.dimensions_breakdown.toSeq,
        accumulator.dimensions_hierarchy ++ value.dimensions_hierarchy,
        accumulator.records_in_baseline_offset + 1
      )
    }
  }

  /**
   * Perform averaging to get offset baseline values
   * @param accumulator
   */
  override def getResult(accumulator: OffsetBaselineAccumulator): AggregatedRecordsWBaseline = {
    AggregatedRecordsWBaseline(
      accumulator.current,
      accumulator.baseline / accumulator.records_in_baseline_offset, // apply averaging
      accumulator.current_dimensions_breakdown,
      accumulator.baseline_dimensions.groupBy(_._1).mapValues(x => x.map(_._2).sum/accumulator.records_in_baseline_offset), // apply averaging in dimensions
      // accumulator.baseline_dimensions.groupBy(_._1).mapValues(x => x.map(_._2).sum/x.length) // this averaging gives false metric overall
      accumulator.dimensions_hierarchy,
      accumulator.records_in_baseline_offset)
  }

  override def merge(a: OffsetBaselineAccumulator, b: OffsetBaselineAccumulator): OffsetBaselineAccumulator = {
    OffsetBaselineAccumulator(
      a.current + b.current,
      a.current_dimensions_breakdown,
      a.baseline + b.baseline,
      a.baseline_dimensions ++ b.baseline_dimensions,
      a.dimensions_hierarchy ++ b.dimensions_hierarchy,
      a.records_in_baseline_offset + b.records_in_baseline_offset
    )
  }
}
