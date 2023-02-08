package models

import utils.Types.{ChildDimension, MetricValue, ParentDimension}

case class AggregatedRecordsWBaseline(
                                       current: MetricValue,
                                       baseline: MetricValue,
                                       current_dimensions_breakdown: Map[Dimension, MetricValue],
                                       baseline_dimensions_breakdown: Map[Dimension, MetricValue],
                                       dimensions_hierarchy: Map[ChildDimension, ParentDimension],
                                       records_in_baseline_offset: Int
                                     )