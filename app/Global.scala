import controllers.ControllerSupport
import play.api.libs.concurrent.Akka
import play.api.mvc.RequestHeader
import play.api.{Application, GlobalSettings}
import scala.concurrent.Future

object Global extends GlobalSettings with ControllerSupport{
  override def onStart(app: Application) {
    super.onStart(app)
    play.Logger.info("Starting the application.")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    play.Logger.info("Stopping the application.")
  }

  override def onRouteRequest(request: RequestHeader) = {
    play.Logger.info("Incoming request: %s" format request)
    super.onRouteRequest(request)
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    import play.api.Play.current
    implicit val dispatcher = Akka.system.dispatcher
    implicit val request0 = request
    Future(CustomNotFound)
  }
}
