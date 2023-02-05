package models

case class RCAResult(
                    current: Double,
                    baseline: Double,
                    dimensionStats: List[DimensionStats]
                  ) {
  override def toString: String = {
    "RCAResult(current=%s, baseline=%s, dimensionStats=%s)".format(current, baseline, dimensionStats.mkString(", "))
  }
}