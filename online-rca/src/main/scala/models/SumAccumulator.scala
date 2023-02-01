package models

case class SumAccumulator(
                           current: Double,
                           start_timestamp: Long,
                           records_accumulated: Int,
                           dimensions_breakdown: Iterable[(Dimension, Double)]
                         )