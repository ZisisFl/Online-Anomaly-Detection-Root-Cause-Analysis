package models

case class AggregatedRecords2(
                               current: Double,
                               start_timestamp: Long,
                               records_aggregated: Int,
                               dimensions_breakdown: Map[Dimension, Double]
                             ) {
  override def toString = {
    "AggregatedRecords(current=%s, start_timestamp=%s, records_aggregated=%s, dimensions_breakdown=%s)".format(
      current,
      start_timestamp,
      records_aggregated,
      dimensions_breakdown
    )
  }
}
