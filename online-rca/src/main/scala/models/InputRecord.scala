package models

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.util.UUID
import utils.Types.{CategoricalDimension, DimensionName}


case class InputRecord(
                        id: String,
                        timestamp: String,
                        value: Double,
                        dimensions: Map[DimensionName, CategoricalDimension],
                        timestamp_pattern: String = "yyyy-MM-DD'T'HH:mm:ssZZZZZ"
                    ) extends Serializable {

  val parsed_timestamp: LocalDateTime =
    LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(timestamp_pattern).withZone(ZoneOffset.UTC))

  override def toString = {
    "InputRecord(id=%s, created_at=%s, value=%s, dimensions=%s)".format(id, timestamp, value, dimensions)
  }
}

object InputRecord {
  def apply(
             timestamp: String,
             value: Double,
             dimensions: Map[DimensionName, CategoricalDimension]
           ): InputRecord = {
    InputRecord(
      id = UUID.randomUUID().toString,
      timestamp = timestamp,
      value = value,
      dimensions = dimensions
    )
  }
}
// multiple constructors for case classes
// https://alvinalexander.com/source-code/scala-how-create-case-class-multiple-alternate-constructors/