package models

case class Dimension[T](name: String, value: T) {

  override def toString = {
    "Dimension(name=%s, value=%s)".format(name, value)
  }
}