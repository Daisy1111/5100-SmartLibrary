package controllers

import play.api.mvc._
import play.api.data.{ Form, Forms }
import models.Defaults

object LoginController extends Controller with ControllerSupport {

  def login = Action {
    implicit request =>
      val correctPassword = play.api.Play.current.configuration.getString("application.admin.password").get

      val loginForm = Form(Forms.single("password" -> Forms.text))

      loginForm.bindFromRequest().fold(
        wrongForm => refererRedirect().flashing("level" -> "error", "msg" -> "Login unsuccessful. Please try again."), {
          _ match {
            case `correctPassword` => refererRedirect().flashing("level" -> "success", "msg" -> "You have been successfully logged in.").withSession(session + ("authenticated" -> "true"))
            case form => refererRedirect().flashing("level" -> "error", "msg" -> "Login unsuccessful. Please try again.")

          }
        })
  }

}
