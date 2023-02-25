package config

import config.AppConfig.InputStream.{DIMENSION_DEFINITIONS, DIMENSION_NAMES}
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.convert.ImplicitConversions.`collection asJava`

class TestConfig extends AnyFlatSpec {

  "InputStream config" should "be loaded" in {
    println(AppConfig.InputStream.VALUE_FIELD)
    println(AppConfig.InputStream.TIMESTAMP_FIELD)
    println(AppConfig.InputStream.DIMENSION_NAMES)
    println(AppConfig.InputStream.DIMENSION_DEFINITIONS.getConfig("ca_city").getString("value_type"))
    DIMENSION_NAMES.forEach(dim_name => println(DIMENSION_DEFINITIONS.getConfig(dim_name)))

    println(AppConfig.InputStream.DIMENSION_HIERARCHIES)

  }

  "dimension levels algorithm" should "work in finite time" in {
    val dimensionLevels = scala.collection.mutable.Map[String, Int]()
    var notDefined = AppConfig.InputStream.DIMENSION_HIERARCHIES
    while (notDefined.nonEmpty) {
      AppConfig.InputStream.DIMENSION_HIERARCHIES.foreach(x => {

        if (x._2 == "root") {
          dimensionLevels(x._1) = 1
          notDefined = notDefined.-(x._1)
        }
        else if (dimensionLevels.isDefinedAt(x._2)) {
          dimensionLevels(x._1) = dimensionLevels(x._2) + 1
          notDefined = notDefined.-(x._1)
        }
      })
    }

    println(dimensionLevels)
  }
}