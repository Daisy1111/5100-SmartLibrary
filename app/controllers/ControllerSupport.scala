package controllers

import play.api.mvc._
import Results._
import scala.concurrent.Future
import play.api.libs.concurrent.Akka

trait ControllerSupport {

  def CustomNotFound(implicit requestHeader: RequestHeader): SimpleResult = NotFound(views.html.notFound())

  def refererRedirect()(implicit request: Request[_]) = {
    Redirect(request.headers.get("Referer").getOrElse("/"))
  }

  object Authenticated extends ActionBuilder[Request] {
    protected def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]): Future[SimpleResult] = {
      import play.api.Play.current
      implicit val dispatcher = Akka.system.dispatcher
      if (request.session.get("authenticated").isDefined)
        block(request)
      else
        Future {
          Redirect(routes.IndexController.index).flashing("level" -> "error", "msg" -> "You are not authorized, please log in")
        }
    }
  }
}
