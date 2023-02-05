package models

import utils.Types.MetricValue

case class AggregatedRecords(
                               current: Double,
                               window_starting_epoch: Long,
                               records_aggregated: Int,
                               dimensions_breakdown: Map[Dimension, MetricValue]
                             ) {
  override def toString = {
    "AggregatedRecords(current=%s, window_starting_epoch=%s, records_aggregated=%s, dimensions_breakdown=%s)".format(
      current,
      window_starting_epoch,
      records_aggregated,
      dimensions_breakdown
    )
  }
}
