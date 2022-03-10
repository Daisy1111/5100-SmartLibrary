package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import Defaults.optionToPk

case class Author(id: Pk[Long], name: String)

object Author {

  val authorParser: RowParser[Author] = get[Pk[Long]]("author.author_id") ~ get[String]("author.name") map {
    case id ~ name => Author(id, name)
  }


  def getAuthor(name: String): Option[Author] = {
    val sql = """select author_id, name from author where name like {author}"""
    DB.withConnection(implicit c =>
      SQL(sql).on("author" -> name).as(authorParser.singleOpt)
    )
  }

  def getAll: List[Author] = {
    val sql = """select author_id, name from author order by name"""
    DB.withConnection(implicit c =>
      SQL(sql).as(authorParser.*)
    )
  }

  /**
   * Insert new author into DB
   * @param author Author to be inserted.
   */
  def saveAuthor(author: Author): Author = {
    val sql = """insert into author values (DEFAULT, {name})"""
    val authorId: Option[Long] = DB.withConnection {
      implicit c =>
        SQL(sql).on("name" -> author.name).executeInsert()
    }

    author.copy(id = authorId)

  }

  def cleanup(): Int = {
    val sql = """delete from author where author_id not in (select distinct author_id from book_author)"""
    DB.withConnection(implicit c=>
      SQL(sql).executeUpdate()
    )
  }
}
