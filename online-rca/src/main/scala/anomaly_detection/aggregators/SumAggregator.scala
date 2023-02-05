package anomaly_detection.aggregators

import models.accumulators.SumAccumulator
import models.{AggregatedRecords, Dimension, InputRecord}
import org.apache.flink.api.common.functions.AggregateFunction

/**
 * SumAggregator applies sum aggregation and breaks down dimensions accordingly
 */
class SumAggregator extends AggregateFunction[InputRecord, SumAccumulator, AggregatedRecords] {
  override def createAccumulator(): SumAccumulator = SumAccumulator(0, 0, 0, Iterable[(Dimension, Double)]())

  override def add(value: InputRecord, accumulator: SumAccumulator): SumAccumulator = {
    // init accumulator.start_timestamp with the first record creating the window
    if (accumulator.window_starting_epoch == 0) {

      SumAccumulator(
        accumulator.current + value.value,
        value.epoch,
        accumulator.records_accumulated + 1,
        accumulator.dimensions_with_metric ++ value.dimensions.values.map(dim => (dim, value.value))
      )
    }
    else {
      SumAccumulator(
        accumulator.current + value.value,
        accumulator.window_starting_epoch,
        accumulator.records_accumulated + 1,
        accumulator.dimensions_with_metric ++ value.dimensions.values.map(dim => (dim, value.value))
      )
    }
  }

  override def getResult(accumulator: SumAccumulator): AggregatedRecords = {
    AggregatedRecords(
      accumulator.current,
      accumulator.window_starting_epoch,
      accumulator.records_accumulated,
      accumulator.dimensions_with_metric.groupBy(_._1).mapValues(_.map(_._2).sum)
    )
  }

  override def merge(a: SumAccumulator, b: SumAccumulator): SumAccumulator = {
    SumAccumulator(
      a.current + b.current,
      a.window_starting_epoch.min(b.window_starting_epoch),
      a.records_accumulated + b.records_accumulated,
      a.dimensions_with_metric ++ b.dimensions_with_metric
    )
  }
}