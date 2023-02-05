package models

import utils.Types.MetricValue

case class AggregatedRecordsWBaseline(
                                       current: MetricValue,
                                       baseline: MetricValue,
                                       current_dimensions_breakdown: Map[Dimension, MetricValue],
                                       baseline_dimensions_breakdown: Map[Dimension, MetricValue],
                                       records_in_baseline_offset: Int
                                     )