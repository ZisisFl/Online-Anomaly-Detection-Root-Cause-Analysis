package models

//case class Dimension(name: String, value: String) {
case class Dimension(name: String, value: String) {

  override def toString = {
    "Dimension(name=%s, value=%s)".format(name, value)
  }
}