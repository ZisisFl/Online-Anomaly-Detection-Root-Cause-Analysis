package models

case class AggregatedRecords(current: Double, start_timestamp: Long, input_records: Array[InputRecord]) {

  override def toString = {
    "AggregatedRecords(current=%s, start_timestamp=%s, input_records)".format(current, start_timestamp)
  }
}