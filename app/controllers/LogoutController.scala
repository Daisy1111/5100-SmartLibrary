package controllers

import play.api.mvc._

object LogoutController extends Controller {
  
  def logout = Action {implicit request =>
    Redirect(routes.IndexController.index).withNewSession.flashing("level" -> "info", "msg" -> "You have been successfully logged out.")
  }
	
}