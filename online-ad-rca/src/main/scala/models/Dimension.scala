package models

import utils.Types.{DimensionGroup, DimensionName, DimensionValue}

case class Dimension(name: DimensionName, value: DimensionValue, group: DimensionGroup) {

  override def toString = {
    "Dimension(name=%s, value=%s, group=%s)".format(name, value, group)
  }
}