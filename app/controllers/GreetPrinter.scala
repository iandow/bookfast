package controllers

import akka.actor.Actor
import play.api.Logger

/**
 * Created by iandow on 7/12/15.
 */
// prints a greeting
class GreetPrinter extends Actor {
  def receive = {
    case Greeting(message) => Logger.info("finished " + message)
  }
}
