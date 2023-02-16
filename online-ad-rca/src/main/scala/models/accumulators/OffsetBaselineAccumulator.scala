package models.accumulators

import models.Dimension
import utils.Types.{ChildDimension, MetricValue, ParentDimension}

case class OffsetBaselineAccumulator(
                                    current: MetricValue,
                                    current_dimensions_breakdown: Map[Dimension, MetricValue],
                                    baseline: MetricValue,
                                    baseline_dimensions: Seq[(Dimension, MetricValue)],
                                    dimensions_hierarchy: Map[ChildDimension, ParentDimension],
                                    records_in_baseline_offset: Int
                                    )
