package models

import java.util.Date
import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import Defaults.optionToPk

case class Book(id: Pk[Long],
                title: String,
                authors: List[Author],
                description: Option[String],
                publisher: Option[String],
                datePublished: Option[Date],
                pageCount: Option[Int],
                contact_information: Option[String],
                tags: List[Tag],
                dateCreated: Date,
                dateModified: Date)

object Book {

  private val bookRowParser =
    get[Pk[Long]]("book_id") ~
      get[String]("title") ~
      Author.authorParser ~
      get[Option[String]]("description") ~
      get[Option[String]]("publisher") ~
      get[Option[Date]]("date_published") ~
      get[Option[Int]]("page_count") ~
      get[Option[String]]("contact_information") ~
      Tag.tagParser ~
      get[Date]("date_created") ~
      get[Date]("date_modified") map {
      // non-existing TupleFlattener for so many elements, cannot use .map(flatten)
      case id ~ title ~ author ~ description ~ publisher ~ datePublished ~ pageCount ~ contact_information ~ tags ~ dateCreated ~ dateModified =>
        (id, title, author, description, publisher, datePublished, pageCount, contact_information, tags, dateCreated, dateModified)
    }

  private val bookQuery =
    """select book.book_id as book_id, title, description, publisher, date_published, page_count, contact_information, author.author_id, author.name, tag.tag_id, tag.name, date_created, date_modified from book
             left join book_author on book.book_id = book_author.book_id inner join author on book_author.author_id = author.author_id
             left join book_tag on book.book_id = book_tag.book_id left join tag on book_tag.tag_id = tag.tag_id """

  private val bookQueryWithId = bookQuery + """where book.book_id = {book_id}"""

  def save(book: Book): Book = {
    DB.withConnection {
      implicit c =>

        val bookId: Option[Long] = book.id match {
          case Id(value) =>
            /* update values */
            val updateSql = """update book set title={title}, description={description}, publisher={publisher}, date_published={datePublished}, page_count = {pageCount}, contact_information={contact_information}, date_modified={dateModified} where book_id = {bookId}"""
            SQL(updateSql).on("title" -> book.title,
              "description" -> book.description, "publisher" -> book.publisher, "datePublished" -> book.datePublished,
              "pageCount" -> book.pageCount, "contact_information" -> book.contact_information, "dateModified" -> new Date(), "bookId" -> value).executeUpdate()

            /* delete join tables for the book (new entries will be stored later) */
            SQL("""delete from book_tag where book_id = {bookId}""").on("bookId" -> value).executeUpdate()
            SQL("""delete from book_author where book_id = {bookId}""").on("bookId" -> value).executeUpdate()

            Some(value)

          case NotAssigned =>
            val sql = """insert into book values (DEFAULT, {title}, {description}, {publisher}, {datePublished}, {pageCount}, {contact_information}, DEFAULT, DEFAULT)"""

            SQL(sql).on("title" -> book.title,
              "description" -> book.description, "publisher" -> book.publisher, "datePublished" -> book.datePublished,
              "pageCount" -> book.pageCount, "contact_information" -> book.contact_information).executeInsert()
        }

        // M:N authors
        book.authors.distinct.map {
          author => Author.getAuthor(author.name).getOrElse(Author.saveAuthor(Author(NotAssigned, author.name))) //todo simplify???
        } foreach (author => {
          SQL( """insert into book_author values ({bookId}, {authorId})""").on("bookId" -> bookId, "authorId" -> author.id).executeInsert()
        })

        // M:N tags
        book.tags.distinct.map {
          tag => Tag.getTag(tag.name).getOrElse(Tag.saveTag(Tag(NotAssigned, tag.name)))
        }.foreach(tag => {
          SQL( """insert into book_tag values ({bookId}, {tagId})""").on("bookId" -> bookId, "tagId" -> tag.id).executeInsert()
        })



        book.copy(id = bookId)
    }

  }

  def getBook(bookId: Long): Option[Book] =
    DB.withConnection {
      implicit c =>
        val sql = bookQueryWithId
        val result = SQL(sql).on("book_id" -> bookId).as(bookRowParser.*)
        result.map(_.copy(_3 = result.map(_._3).distinct, _9 = result.map(_._9).flatten.distinct)).headOption.map(tuple => (Book.apply _).tupled(tuple))
    }

  def getBooks(authorId: Option[Long] = None, tagId: Option[Long] = None): List[Book] = {
    DB.withConnection {
      implicit c =>
        val list = SQL(bookQuery).as(bookRowParser.*).groupBy(_._1).values.map {
          tupleList => tupleList.map(_.copy(_3 = tupleList.map(_._3).distinct, _9 = tupleList.map(_._9).flatten.distinct)).headOption
        }.flatten.map(tuple => (Book.apply _).tupled(tuple)).toList

        val authorFilteredList = authorId match {
          case Some(id) => list.filter(_.authors.exists(_.id.get == id))
          case None => list
        }

        (tagId match {
          case Some(id) => authorFilteredList.filter(_.tags.exists(_.id.get == id))
          case None => authorFilteredList
        }).sortWith((b1, b2) => b1.dateCreated.after(b2.dateCreated))
    }
  }

  def deleteBook(id: Long) = {
    DB.withConnection {
      implicit c =>
        SQL("delete from book where book_id = {id}").on("id" -> id).executeUpdate()
    }
  }
}

case class SearchResult(query: String, totalResults: Int, page: Int, results: List[Book])