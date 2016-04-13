
/*
* Copyright 2015-2016 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package controllers

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Inbox, Props}
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import play.api.Play

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
    val formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
    val today = Calendar.getInstance().getTime()
    //    val date = formatter.parseDateTime("1/18/2016")
    val format1 = new SimpleDateFormat("MM/dd/yyyy");
    val date2 = format1.format(today)
    val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
    greeter.tell(DateToCheck(date.toString("MM/dd/yyyy")), ActorRef.noSender)

    // Ask the 'greeter for the latest 'greeting'
    // Reply should go to the "actor-in-a-box"
    inbox.send(greeter, Greet)

    // Wait 900 seconds for the reply with the 'greeting' message
    val Greeting(message1) = inbox.receive(new FiniteDuration(900,duration.SECONDS))
    val greetPrinter = system.actorOf(Props[GreetPrinter])

    // after zero seconds, send a Greet message every 15 minutes to the greeter with a sender of the greetPrinter
    system.scheduler.schedule(new FiniteDuration(0, duration.SECONDS), new FiniteDuration(Play.current.configuration.getLong("jsoup_polling_interval").getOrElse(900), duration.SECONDS), greeter, Greet)(system.dispatcher, greetPrinter)
  }

}
