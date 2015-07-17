package controllers

import akka.actor.Actor

/**
 * Created by iandow on 7/12/15.
 */
// prints a greeting
class GreetPrinter extends Actor {
  def receive = {
    case Greeting(message) => println(message)
  }
}
