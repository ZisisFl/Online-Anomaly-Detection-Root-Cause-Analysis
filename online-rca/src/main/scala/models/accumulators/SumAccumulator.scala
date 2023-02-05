package models.accumulators

import models.Dimension
import utils.Types.MetricValue

case class SumAccumulator(
                           current: MetricValue,
                           window_starting_epoch: Long,
                           records_accumulated: Int,
                           dimensions_with_metric: Seq[(Dimension, MetricValue)]
                         )