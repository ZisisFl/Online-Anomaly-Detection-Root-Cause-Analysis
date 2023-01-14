package models

// idea create numerical and categorical dimension classes
case class Dimension(name: String, value: String) {

  override def toString = {
    "Dimension(name=%s, value=%s)".format(name, value)
  }
}

//object Dimension
// multiple constructors for case classes
// https://alvinalexander.com/source-code/scala-how-create-case-class-multiple-alternate-constructors/