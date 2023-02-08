package utils

import models.Dimension
import config.AppConfig
import utils.Types.{ChildDimension, DimensionName, ParentDimension}

object DimensionHierarchiesBuilder {

  def buildHierarchies(dimensions: Map[DimensionName, Dimension]): Map[ChildDimension, ParentDimension] = {
    AppConfig.InputStream.DIMENSION_HIERARCHIES
      .filter(item => item._2 != "root") // filter out dimension whose parent is root
      .map(dimensionHierarchy => (
        dimensions.get(dimensionHierarchy._1).get,
        dimensions.get(dimensionHierarchy._2).get)
      )
  }
}
