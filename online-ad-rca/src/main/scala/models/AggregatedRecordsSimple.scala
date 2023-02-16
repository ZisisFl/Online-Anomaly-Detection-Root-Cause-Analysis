package models

@deprecated
case class AggregatedRecordsSimple(current: Double, start_timestamp: Long, input_records: Array[InputRecord]) {

  override def toString = {
    "AggregatedRecords(current=%s, start_timestamp=%s, len=%s, input_records=%s)".format(current, start_timestamp, input_records.mkString(","))
  }
}