package anomaly_detection.aggregators

import models.{AggregatedRecords, InputRecord}
import org.apache.flink.api.common.functions.AggregateFunction

class SumAggregator extends AggregateFunction[InputRecord, AggregatedRecords, AggregatedRecords] {
  override def createAccumulator(): AggregatedRecords = AggregatedRecords(0, 0, Array[InputRecord]())

  override def add(value: InputRecord, accumulator: AggregatedRecords): AggregatedRecords = {
    if (accumulator.start_timestamp == 0) {
      AggregatedRecords(accumulator.current + value.value, value.epoch, accumulator.input_records :+ value)
    }
    else {
      AggregatedRecords(accumulator.current + value.value, accumulator.start_timestamp, accumulator.input_records :+ value)
    }
  }

  override def getResult(accumulator: AggregatedRecords): AggregatedRecords = AggregatedRecords(
    accumulator.current,
    accumulator.start_timestamp,
    accumulator.input_records
  )

  override def merge(a: AggregatedRecords, b: AggregatedRecords): AggregatedRecords = {
    AggregatedRecords(a.current + b.current, a.start_timestamp.min(b.start_timestamp), a.input_records ++ b.input_records)
  }
}