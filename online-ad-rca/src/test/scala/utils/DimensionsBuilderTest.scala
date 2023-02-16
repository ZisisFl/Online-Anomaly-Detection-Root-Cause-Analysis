package utils

import config.AppConfig
import config.AppConfig.InputStream.DIMENSION_DEFINITIONS
import org.scalatest.flatspec.AnyFlatSpec
import utils.dimension.DimensionsBuilder

class DimensionsBuilderTest extends AnyFlatSpec {
  "dimension hierarchies" should "be built" in {
    val hierarchiesMap = AppConfig.InputStream.DIMENSION_NAMES
      .map(dim => (dim, DIMENSION_DEFINITIONS.getConfig(dim).getString("parent_dimension")))
      .groupBy(_._1)
      .mapValues(_.map(_._2).head)

    print(hierarchiesMap)

    print(hierarchiesMap.filter(item => item._2 == "root"))
  }
}