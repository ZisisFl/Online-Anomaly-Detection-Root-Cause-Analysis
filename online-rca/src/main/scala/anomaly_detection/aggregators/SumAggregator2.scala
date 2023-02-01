package anomaly_detection.aggregators

import models.{AggregatedRecords, AggregatedRecords2, Dimension, InputRecord, SumAccumulator}
import org.apache.flink.api.common.functions.AggregateFunction

class SumAggregator2 extends AggregateFunction[InputRecord, SumAccumulator, AggregatedRecords2] {
  override def createAccumulator(): SumAccumulator = SumAccumulator(0, 0, 0, Iterable[(Dimension, Double)]())

  override def add(value: InputRecord, accumulator: SumAccumulator): SumAccumulator = {
    // init accumulator.start_timestamp with the first record creating the window
    if (accumulator.start_timestamp == 0) {

      SumAccumulator(
        accumulator.current + value.value,
        value.epoch,
        accumulator.records_accumulated + 1,
        accumulator.dimensions_breakdown ++ value.dimensions.values.map(dim => (dim, value.value))
      )
    }
    else {
      SumAccumulator(
        accumulator.current + value.value,
        accumulator.start_timestamp,
        accumulator.records_accumulated + 1,
        accumulator.dimensions_breakdown ++ value.dimensions.values.map(dim => (dim, value.value))
      )
    }
  }

  override def getResult(accumulator: SumAccumulator): AggregatedRecords2 = {
    AggregatedRecords2(
      accumulator.current,
      accumulator.start_timestamp,
      accumulator.records_accumulated,
      accumulator.dimensions_breakdown.groupBy(_._1).mapValues(_.map(_._2).sum)
    )
  }

  override def merge(a: SumAccumulator, b: SumAccumulator): SumAccumulator = {
    SumAccumulator(
      a.current + b.current,
      a.start_timestamp.min(b.start_timestamp),
      a.records_accumulated + b.records_accumulated,
      a.dimensions_breakdown ++ b.dimensions_breakdown
    )
  }
}