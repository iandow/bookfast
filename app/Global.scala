import controllers.HelloAkkaScala
import play.api._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    HelloAkkaScala.start();
  }
}