package controllers

import play.api.mvc._
import models.Book
import play.api.Play
import play.api.Play.current

object IndexController extends Controller {

  def index = Action { implicit request =>
    val books = Book.getBooks()

    val bookCount = Play.configuration.getInt("application.smartpageBookCount").get
    Ok(views.html.index(books.take(bookCount)))
  }

}