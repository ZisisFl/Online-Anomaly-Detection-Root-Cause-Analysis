package models

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

case class AnomalyEvent(aggregatedRecordsWBaseline: AggregatedRecordsWBaseline) {
  val anomalyId: String = UUID.randomUUID().toString
  val createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
  val epoch: Long = createdAt.toEpochSecond(ZoneOffset.UTC)

  override def toString = {
    "AnomalyEvent(anomalyId=%s, createdAt=%s, aggregatedRecordsWBaseline=%s)".format(anomalyId, createdAt.toString, AggregatedRecordsWBaseline)
  }
}
