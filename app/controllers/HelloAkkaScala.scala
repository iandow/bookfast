package controllers

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Inbox, Props}
import org.joda.time.Seconds

import scala.concurrent.duration
import scala.concurrent.duration.FiniteDuration

/**
 * Created by iandow on 7/12/15.
 */
object HelloAkkaScala extends App {

  def start() {

    // Create the 'helloakka' actor system
    val system = ActorSystem("helloakka")

    // Create the 'greeter' actor
    val greeter = system.actorOf(Props[Greeter], "greeter")

    // Create an "actor-in-a-box"
    val inbox = Inbox.create(system)

    // Tell the 'greeter' to change its 'greeting' message
    greeter.tell(DateToCheck("1/9/2016"), ActorRef.noSender)

    // Ask the 'greeter for the latest 'greeting'
    // Reply should go to the "actor-in-a-box"
    inbox.send(greeter, Greet)

    // Wait 5 seconds for the reply with the 'greeting' message
    val Greeting(message1) = inbox.receive(new FiniteDuration(5,duration.SECONDS))
    println(s"Akka got her result: $message1")

    val greetPrinter = system.actorOf(Props[GreetPrinter])


    // after zero seconds, send a Greet message every second to the greeter with a sender of the greetPrinter
    system.scheduler.schedule(new FiniteDuration(0, duration.SECONDS), new FiniteDuration(900, duration.SECONDS), greeter, Greet)(system.dispatcher, greetPrinter)
  }

}
