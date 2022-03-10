package controllers

import play.api.mvc._
import play.api._
import models.{Tag, Author}

object UtilController extends Controller with ControllerSupport {
  def listConfiguration = Authenticated {
    implicit request =>
      val keys = Play.current.configuration.keys.toList.sortWith(_ < _)
      Ok(keys.mkString("\n"))
  }

  def cleanup = Authenticated {
    implicit request =>
      val x = Author.cleanup()
      val y = Tag.cleanup()
      Ok((x + y).toString)
  }
}