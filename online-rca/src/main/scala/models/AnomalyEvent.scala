package models

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

case class AnomalyEvent() {
  val id: String = UUID.randomUUID().toString
  val created_at: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

  override def toString = {
    "AnomalyEvent(id=%s, created_at=%s)".format(id, created_at.toString)
  }
}
