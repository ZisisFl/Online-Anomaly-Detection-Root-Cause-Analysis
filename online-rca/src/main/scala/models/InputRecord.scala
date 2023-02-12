package models

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.util.UUID
import utils.Types.{ChildDimension, DimensionName, ParentDimension}

case class InputRecord(
                        id: String,
                        timestamp: String,
                        value: Double,
                        dimensions: Map[DimensionName, Dimension],
                        dimensions_hierarchy: Map[ChildDimension, ParentDimension],
                        timestampPattern: String = "yyyy-MM-dd'T'HH:mm:ss"
                    ) extends Serializable {

  val parsed_timestamp: LocalDateTime =
    LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(timestampPattern).withZone(ZoneOffset.UTC))

  val epoch: Long = parsed_timestamp.toEpochSecond(ZoneOffset.UTC)

  override def toString = {
    "InputRecord(id=%s, created_at=%s, value=%s, dimensions=%s, dimensions_hierarchy=%s)".format(id, timestamp, value, dimensions, dimensions_hierarchy)
  }
}

object InputRecord {
  def apply(
             timestamp: String,
             value: Double,
             dimensions: Map[DimensionName, Dimension],
             dimensions_hierarchy: Map[ChildDimension, ParentDimension]
           ): InputRecord = {
    InputRecord(
      id = UUID.randomUUID().toString,
      timestamp = timestamp,
      value = value,
      dimensions = dimensions,
      dimensions_hierarchy = dimensions_hierarchy
    )
  }
}
// multiple constructors for case classes
// https://alvinalexander.com/source-code/scala-how-create-case-class-multiple-alternate-constructors/