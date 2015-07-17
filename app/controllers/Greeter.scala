package controllers

import akka.actor.Actor
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import scala.collection.JavaConverters._
import models.{Availability, Availabilities}

/**
 * Created by iandow on 7/12/15.
 */
class Greeter extends Actor {
  var greeting = ""

  def receive = {
    case DateToCheck(who) => greeting = s"checking date:, $who"
    case Greet => sender ! Greeting(greeting) // Send the current greeting back to the sender

      val parkid = "75098";
      val formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
      val date = formatter.parseDateTime("1/9/2016");
      val url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate=" + date.toString("MM/dd/yyyy") + "&contractCode=NRSO&parkId=" + parkid
      System.out.println(url)
      val doc = Jsoup.connect(url).userAgent("Mozilla/5.0")
        .timeout(5000).get()
      val calendar_elements = doc.select("td[class^=status")


      for ((day,i) <- calendar_elements.asScala.zipWithIndex) {
        if (day.text() == "A" || day.text() == "a") {
          println("Available " + date.plusDays(i).toString("MM/dd/yyyy") + " " + parkid.toInt)

          //TODO: prevent duplciate adds.

          val size = Availabilities.all.size
          println("size: ", size)
          val d = date.plusDays(i).toString("MM/dd/yyyy")
          val record = Availabilities.find(d,parkid.toInt)
          println("Found: ", record)

          if (size == 0 || record == None) {
            println("inserting record")
            val a = Availability("5milebutte", d, parkid.toInt)
            Availabilities.create(a)
          }

//          println(Availabilities.find(date.plusDays(i).toString("MM/dd/yyyy"), parkid.toInt).toString)
//          }
        }

      }

  }
}
