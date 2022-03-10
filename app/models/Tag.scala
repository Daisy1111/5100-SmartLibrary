package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import anorm.~
import Defaults.optionToPk
import play.api.Play.current

case class Tag(id: Pk[Long], name: String)

object Tag {
  val tagParser: RowParser[Option[Tag]] = (get[Option[Long]]("tag.tag_id") ~ get[Option[String]]("tag.name")) map {
    case a ~ Some(b) => Some(Tag(a, b))
    case _ => None
  }

  def getTag(name: String): Option[Tag] = {
    val sql = """select tag_id, tag.name from tag where name like {tag}"""
    DB.withConnection(implicit c =>
      SQL(sql).on("tag" -> name).as(tagParser.singleOpt) match {
        case Some(x) => x
        case None => None
      }
    )
  }

  def getAll: List[Tag] = {
    val sql = """select tag_id, tag.name from tag order by tag.name"""
    DB.withConnection(implicit c =>
      SQL(sql).as(tagParser.*).flatten
    )
  }

  def saveTag(tag: Tag): Tag = {
    val sql = """insert into tag values (DEFAULT, {name})"""
    val tagId: Option[Long] = DB.withConnection {
      implicit c =>
        SQL(sql).on("name" -> tag.name).executeInsert()
    }

    tag.copy(id = tagId)

  }

  def cleanup(): Int = {
    val sql = """delete from tag where tag_id not in (select distinct tag_id from book_tag)"""
    DB.withConnection(implicit c=>
      SQL(sql).executeUpdate()
    )
  }

}
