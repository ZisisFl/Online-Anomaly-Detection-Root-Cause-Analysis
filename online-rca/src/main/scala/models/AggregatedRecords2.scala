package models

case class AggregatedRecords2(current: Double, start_timestamp: Long, input_records: Map[Dimension, Double]) {

  override def toString = {
    "AggregatedRecords(current=%s, start_timestamp=%s, input_records=%s)".format(current, start_timestamp, input_records)
  }
}
