package models

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

case class AnomalyEvent(aggregatedRecordsWBaseline: AggregatedRecordsWBaseline) {
  val anomalyId: String = UUID.randomUUID().toString
  val detectedAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
  val epoch: Long = detectedAt.toEpochSecond(ZoneOffset.UTC)

  override def toString = {
    "AnomalyEvent(anomalyId=%s, detectedAt=%s, aggregatedRecordsWBaseline=%s)".format(anomalyId, detectedAt.toString, aggregatedRecordsWBaseline)
  }
}