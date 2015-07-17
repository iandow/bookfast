package controllers

import akka.actor.ActorSystem
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.jsoup
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import scala.concurrent.duration.Duration
import scala.slick.driver.PostgresDriver.simple._
import models.Site
import models.Sites
import models.Availability
import models.Availabilities
import models.Subscription
import models.Subscriptions

object Application extends Controller {
  def index = Action {
    Ok(views.html.sites.index(Sites.all))
  }

  def availindex = Action {
    Ok(views.html.availabilities.index(Availabilities.all))
  }

  def subscriptionindex = Action {
    Ok(views.html.subscriptions.index(Subscriptions.all))
  }

  def recreation = Action {
    val parkid = "75098";
    val formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
    val date = formatter.parseDateTime("1/9/2016");
    val url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid
    System.out.println(url)
    val doc = Jsoup.connect(url).userAgent("Mozilla/5.0")
    		.timeout(5000).get()

//    System.out.println(doc.body().toString());
    val calendar_elements = doc.select("td[class^=status")
//    var dailystatus = new Array[String](14)
//    for (element <- calendar_elements) {
//      dailystatus[i] = element.owntext()
//    }

    Ok(views.html.recreation(date, calendar_elements))
// 		Ok(views.html.recreation(doc.select("td[class^=status").toString))
// 		Ok(views.html.recreation(doc.select("td[class=status r]").toString))
 	}

}
