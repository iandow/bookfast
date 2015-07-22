package controllers

import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Calendar
import org.jsoup.nodes.Document

import scala.collection.JavaConversions._
import play.api.{Play, Logger}
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import play.api.mvc._
import models.Sites
import models.Availabilities
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
    var sites = Sites.all
    val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")
    val today = Calendar.getInstance().getTime()
    val format1 = new SimpleDateFormat("MM/dd/yyyy");
    val date2 = format1.format(today)
    val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
    var i = 0
    val sitenames = new Array[String](sites.size)
    val parkurls = new Array[String](sites.size)
    val biweekArray = new Array[Elements](sites.size)
    val biweek2Array = new Array[Elements](sites.size)

    for(site <- sites) {
      var url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+site.parkid
      var url2 = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate=" + date.plusDays(14).toString("MM/dd/yyyy") + "&contractCode=NRSO&parkId=" + site.parkid
      //System.out.println(url)
      var doc, doc2 = new Document("")
      try {
        doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(Play.current.configuration.getInt("httptimeout").getOrElse(0)).get()
        doc2 = Jsoup.connect(url2).userAgent("Mozilla/5.0").timeout(Play.current.configuration.getInt("httptimeout").getOrElse(0)).get()
      } catch {
        case e: SocketTimeoutException => Logger.warn("Socket timeout exception");
        case e: Exception => Logger.error("exception caught: " + e);
      }
      val biweek = doc.select("td[class^=status")
      val biweek2 = doc2.select("td[class^=status")

      biweekArray(i) = biweek
      biweek2Array(i) = biweek2
      sitenames(i) = site.name
      parkurls(i) = url
      i += 1
    }

    Ok(views.html.recreation(date, sitenames, parkurls, biweekArray, biweek2Array))
// 		Ok(views.html.recreation(doc.select("td[class^=status").toString))
// 		Ok(views.html.recreation(doc.select("td[class=status r]").toString))
 	}

}
