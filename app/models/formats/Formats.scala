package models.formats

import models.{Author, Book}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date
import anorm.NotAssigned

object Formats {
  implicit val bookReads: Reads[Book] = (
    (__ \ "title").readNullable[String] ~
      (__ \ "authors").readNullable[List[String]] ~
      (__ \ "description").readNullable[String] ~
      (__ \ "publisher").readNullable[String] ~
      (__ \ "publishedDate").lazyReadNullable[Date](Reads.dateReads("yyyy-MM-dd") or Reads.dateReads("yyyy")) ~
      (__ \ "pageCount").readNullable[Int]
   ).tupled.map {
    case (title, authors, description, publisher, publishedDate, pageCount) =>
      Book(NotAssigned, title.getOrElse(""), authors.map(_.map(name => Author(NotAssigned, name))).getOrElse(List()),
        description, publisher, publishedDate, pageCount, None, List(), new Date(), new Date())
  }
}