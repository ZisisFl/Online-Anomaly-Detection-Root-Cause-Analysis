package anomaly_detection.aggregators

import models.{AggregatedRecords, InputRecord}
import org.apache.flink.api.common.functions.AggregateFunction

class SumAggregator extends AggregateFunction[InputRecord, AggregatedRecords, AggregatedRecords] {
  override def createAccumulator(): AggregatedRecords = AggregatedRecords(0, Array[InputRecord]())

  override def add(value: InputRecord, accumulator: AggregatedRecords): AggregatedRecords = {
    AggregatedRecords(accumulator.current + value.value, accumulator.input_records :+ value)
  }

  override def getResult(accumulator: AggregatedRecords): AggregatedRecords = AggregatedRecords(accumulator.current, accumulator.input_records)

  override def merge(a: AggregatedRecords, b: AggregatedRecords): AggregatedRecords = {
    AggregatedRecords(a.current + b.current, a.input_records ++ b.input_records)
  }
}