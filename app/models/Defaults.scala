package models

import anorm.{NotAssigned, Id, Pk}

object Defaults {

  import scala.language.implicitConversions

  /*
   * Implicit conversion from Option[T] to Pk[T] (because of the annoying executeInsert() returning Option[Long]
   */
  implicit def optionToPk[T](opt: Option[T]): Pk[T] = opt match {
    case Some(x) => Id(x)
    case None => NotAssigned
  }

  implicit def pkToOption[T](pk: Pk[T]): Option[T] = pk match {
    case Id(value) => Some(value)
    case NotAssigned => None
  }

}
