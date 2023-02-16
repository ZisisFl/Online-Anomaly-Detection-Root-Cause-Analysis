package models

import java.time.LocalDateTime
case class RCAResult(
                    relatedAnomalyId: String,
                    detectedAt: LocalDateTime,
                    currentTotal: Double,
                    baselineTotal: Double,
                    dimensionSummaries: List[DimensionSummary]
                  ) {
  override def toString: String = {
    "RCAResult(relatedAnomalyId=%s, detectedAt=%s, currentTotal=%s, baselineTotal=%s, dimensionSummaries=%s)".format(
      relatedAnomalyId,
      detectedAt,
      currentTotal,
      baselineTotal,
      dimensionSummaries.mkString(", ")
    )
  }
}