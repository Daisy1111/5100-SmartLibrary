package controllers

import play.api.mvc.{Action, Controller}
import models.{Book, Tag}
import play.api.libs.json.Json


object TagController extends Controller {
  def tagPopoverContent(tagId: Long) = Action {
    implicit request =>
      Ok(views.html.tags.tagPopoverContent(Book.getBooks(tagId = Some(tagId))))
  }

}
