package controllers

import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.json._
import models.{Tag, Author, Defaults, Book}
import play.api.data.Form
import play.api.data.Forms._
import anorm.NotAssigned
import Defaults.optionToPk
import Defaults.pkToOption
import java.util.Date
import scala.Some
import play.api.libs.concurrent.Akka

object BookController extends Controller with ControllerSupport {

  val delimiter: String = ","

  def list(query: String) = Action {
    implicit request =>
      val books = Book.getBooks().filter(_.title.toLowerCase.contains(query.toLowerCase))
      Ok(views.html.books.list(books, Author.getAll, Tag.getAll))
  }

  def listFragment(query: String, authorId: Option[Long], tagId: Option[Long]) = Action {
    implicit request =>
      val books = Book.getBooks().filter(_.title.toLowerCase.contains(query.toLowerCase)).
        filter(book => !authorId.isDefined || book.authors.exists(_.id.get == authorId.get)).
        filter(book => !tagId.isDefined || book.tags.exists(_.id.get == tagId.get)).
        sortWith(_.title < _.title)
      Ok(views.html.tags.bookList(books))
  }

  def detail(identifier: Long) = Action {
    implicit request =>
      Book.getBook(identifier).map(b => Ok(views.html.books.detail(b))).getOrElse(CustomNotFound)
  }

  def edit(identifier: Long) = Authenticated {
    implicit request =>
      val book = Book.getBook(identifier)
      book match {
        case Some(book0) => Ok(views.html.books.add.manual(addForm.fill(book0)))
        case None => CustomNotFound
      }


  }

  def save = Authenticated {
    implicit request =>
      addForm.bindFromRequest.fold(
        form => {
          Ok(views.html.books.add.manual(form))
        },
        book => {
          val savedBook = Book.save(book)
          Redirect(routes.BookController.detail(savedBook.id.get))
        })
  }

  def delete(identifier: Long) = Authenticated {
    implicit request =>
      val result = Book.deleteBook(identifier)

      if (result == 1)
        Ok.flashing("level" -> "success", "msg" -> "Book has been successfully deleted")
      else
        NotFound
  }

  private val addForm: Form[Book] = Form(mapping(
    "id" -> optional(longNumber),
    "title" -> nonEmptyText,
    "authors" -> nonEmptyText,
    "publisher" -> optional(text),
    "datePublished" -> optional(date("yyyy").verifying("date.notInRange", date => true)), //todo correct validation
    "description" -> optional(text),
    "contact_information" -> optional(text),
    "pageCount" -> optional(number(min = 1)),
    "tags" -> optional(text)) {
    case (id, title, authors, publisher, datePublished, description, contact_information, pageCount, tags) =>
      Book(id, title, authors.split(delimiter).toList.map(_.trim).filter(!_.isEmpty).map(Author(NotAssigned, _)), description, publisher,
        datePublished, pageCount, contact_information, tags.map {
          _.split(delimiter).toList.map(_.trim).filter(!_.isEmpty).map(Tag(NotAssigned, _))
        }.getOrElse(Nil), new Date(), new Date())
  }(book => Some(book.id, book.title, book.authors.map(_.name).mkString(delimiter),
    book.publisher, book.datePublished, book.description, book.contact_information, book.pageCount, book.tags match {
      case Nil => None
      case list => Some(list.map(_.name).mkString(delimiter))
    })))

  def addManualForm() = Authenticated {
    implicit request =>
      Ok(views.html.books.add.manual(addForm))
  }
}