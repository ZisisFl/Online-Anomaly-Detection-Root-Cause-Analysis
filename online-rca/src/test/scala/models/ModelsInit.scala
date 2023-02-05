package models

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelsInit extends AnyFlatSpec {

  "Dimension instance" should "be initialized" in {
    val dim: Dimension = Dimension(name="name", value="zisis")
    println(dim)
  }

  "InputRecord instance" should "be initialized" in {
    val input_record: InputRecord = InputRecord(
      id = "fgt",
      timestamp = "1998-01-01T22:07:58+00:00",
      value = 234.0f,
      dimensions= Map("sm_type" -> Dimension("sm_type", "OVERNIGHT")))

    print(input_record)
  }
}
