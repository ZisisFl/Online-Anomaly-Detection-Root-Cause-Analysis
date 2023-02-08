package models

case class RCAResult(
                    current: Double,
                    baseline: Double,
                    dimensionSummaries: List[DimensionSummary]
                  ) {
  override def toString: String = {
    "RCAResult(current=%s, baseline=%s, dimensionStats=%s)".format(current, baseline, dimensionSummaries.mkString(", "))
  }
}