package models

import utils.Types.{DimensionName, DimensionValue}

case class Dimension(name: DimensionName, value: DimensionValue) {

  override def toString = {
    "Dimension(name=%s, value=%s)".format(name, value)
  }
}