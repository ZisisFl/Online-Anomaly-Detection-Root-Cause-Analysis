package anomaly_detection.aggregators

import models.{AggregatedRecordsSimple, InputRecord}
import org.apache.flink.api.common.functions.AggregateFunction

@deprecated
class SumAggregatorSimple extends AggregateFunction[InputRecord, AggregatedRecordsSimple, AggregatedRecordsSimple] {
  override def createAccumulator(): AggregatedRecordsSimple = AggregatedRecordsSimple(0, 0, Array[InputRecord]())

  override def add(value: InputRecord, accumulator: AggregatedRecordsSimple): AggregatedRecordsSimple = {
    // init accumulator.start_timestamp with the first record creating the window
    if (accumulator.start_timestamp == 0) {
      AggregatedRecordsSimple(accumulator.current + value.value, value.epoch, accumulator.input_records :+ value)
    }
    else {
      AggregatedRecordsSimple(accumulator.current + value.value, accumulator.start_timestamp, accumulator.input_records :+ value)
    }
  }

  override def getResult(accumulator: AggregatedRecordsSimple): AggregatedRecordsSimple = AggregatedRecordsSimple(
    accumulator.current,
    accumulator.start_timestamp,
    accumulator.input_records
  )

  override def merge(a: AggregatedRecordsSimple, b: AggregatedRecordsSimple): AggregatedRecordsSimple = {
    AggregatedRecordsSimple(a.current + b.current, a.start_timestamp.min(b.start_timestamp), a.input_records ++ b.input_records)
  }
}