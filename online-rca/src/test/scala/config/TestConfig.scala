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
  }
}