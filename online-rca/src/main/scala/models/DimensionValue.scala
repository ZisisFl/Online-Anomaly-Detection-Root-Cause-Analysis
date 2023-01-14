package models
//https://stackoverflow.com/questions/43495047/scala-variable-with-multiple-types
//https://stackoverflow.com/questions/32406569/scala-type-alias-how-to-have-a-type-that-represent-multiple-data-types

//sealed trait DimensionValue
//case class NumericDimension(value: Int) extends DimensionValue
//case class CategoricalDimension(value: String) extends DimensionValue

//sealed trait DimensionLike
//case class NumericalDimension(value: Int) extends DimensionValue
//case class CategoricalDimension(value: String) extends DimensionValue
//
//abstract class DimensionValue[T <: DimensionLike] {
//  val value: DimensionLike = ???
//  value match {
//    case NumericalDimension
//  }
//}

class DimensionValue[A](value: A) {

}