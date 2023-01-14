import models.{AnomalyEvent, Dimension, InputRecord}

object Test {
  def main(args: Array[String]): Unit = {
    val yo: Dimension = Dimension(name="yo", value = "yolo")
    println(yo)

    val anomaly: AnomalyEvent = AnomalyEvent()
    println(anomaly)

    val input_record: InputRecord = InputRecord(
      id = "fgt",
      timestamp = "1998-01-01T22:07:58+00:00",
      value = 234.0f,
      dimensions= List(Dimension("sm_type", "OVERNIGHT")))

    print(input_record)
  }
}
