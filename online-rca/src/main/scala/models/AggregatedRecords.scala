package models

import utils.Types.{ChildDimension, MetricValue, ParentDimension}

case class AggregatedRecords(
                               current: Double,
                               window_starting_epoch: Long,
                               records_aggregated: Int,
                               dimensions_breakdown: Map[Dimension, MetricValue],
                               dimensions_hierarchy: Map[ChildDimension, ParentDimension]
                             ) {
  override def toString = {
    "AggregatedRecords(current=%s, window_starting_epoch=%s, records_aggregated=%s, dimensions_breakdown=%s, dimensions_hierarchy=%s)".format(
      current,
      window_starting_epoch,
      records_aggregated,
      dimensions_breakdown,
      dimensions_hierarchy
    )
  }
}
