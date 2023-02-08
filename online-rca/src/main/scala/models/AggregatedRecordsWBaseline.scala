package models

import utils.Types.{ChildDimension, MetricValue, ParentDimension}

case class AggregatedRecordsWBaseline(
                                       current: MetricValue,
                                       baseline: MetricValue,
                                       current_dimensions_breakdown: Map[Dimension, MetricValue],
                                       baseline_dimensions_breakdown: Map[Dimension, MetricValue],
                                       dimensions_hierarchy: Map[ChildDimension, ParentDimension],
                                       records_in_baseline_offset: Int
                                     ) {
  override def toString = {
    "AggregatedRecordsWBaseline(current=%s, baseline=%s, current_dimensions_breakdown=%s, baseline_dimensions_breakdown=%s, dimensions_hierarchy=%s, records_in_baseline_offset=%s)".format(
      current,
      baseline,
      current_dimensions_breakdown,
      baseline_dimensions_breakdown,
      dimensions_hierarchy,
      records_in_baseline_offset
    )
  }
}