package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Akka

object FlagController extends Controller {

  private val flagDirectory = "/public/images/flags/"

  private val defaultFlag = flagDirectory + "default.png"

  private def mkStream(filePath: String) = getClass.getResourceAsStream(filePath)

  def getFlag(code: String) = Action {

    val flagStream = mkStream(flagDirectory + code + ".png")
    import play.api.Play.current
    implicit val dispatcher = Akka.system.dispatcher

    if (flagStream != null)
      Ok.chunked(Enumerator.fromStream(flagStream)).as("image/png")
    else
      Ok.chunked(Enumerator.fromStream(mkStream(defaultFlag))).as("image/png")
  }
}
