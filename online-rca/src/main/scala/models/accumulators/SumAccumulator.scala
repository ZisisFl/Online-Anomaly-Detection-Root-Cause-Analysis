package models.accumulators

import models.Dimension
import utils.Types.MetricValue

case class SumAccumulator(
                           current: Double,
                           start_timestamp: Long,
                           records_accumulated: Int,
                           dimensions_with_metric: Iterable[(Dimension, MetricValue)]
                         )