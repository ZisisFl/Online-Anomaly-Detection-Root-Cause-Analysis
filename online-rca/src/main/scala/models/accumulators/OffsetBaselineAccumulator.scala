package models.accumulators

import models.Dimension
import utils.Types.MetricValue

case class OffsetBaselineAccumulator(
                                    current: MetricValue,
                                    current_dimensions_breakdown: Map[Dimension, MetricValue],
                                    baseline: MetricValue,
                                    baseline_dimensions: Seq[(Dimension, MetricValue)],
                                    records_in_baseline_offset: Int
                                    )
