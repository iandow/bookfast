package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.JavaConversions._
import play.api.Logger
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
    var parkid = "75098";
    val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")
    val today = Calendar.getInstance().getTime()
    val format1 = new SimpleDateFormat("MM/dd/yyyy");
    val date2 = format1.format(today)
    val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
    var url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid
    //System.out.println(url)
    var doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get()
    var biweek = doc.select("td[class^=status")
    var doc2 = Jsoup.connect("http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.plusDays(14).toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid).userAgent("Mozilla/5.0").timeout(5000).get()
    var biweek2 = doc2.select("td[class^=status")
    val biweekArray = new Array[Elements](3)
    biweekArray(0) = biweek
    val biweek2Array = new Array[Elements](3)
    biweek2Array(0) = biweek2
    val sitenames = new Array[String](3)
    sitenames(0) = "File Mile Butte"
    val parkurls = new Array[String](3)
    parkurls(0) = url



    parkid = "75097";
    url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid
    //System.out.println(url)
    doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get()
    biweek = doc.select("td[class^=status")
    doc2 = Jsoup.connect("http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.plusDays(14).toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid).userAgent("Mozilla/5.0").timeout(5000).get()
    biweek2 = doc2.select("td[class^=status")
    biweekArray(1) = biweek
    biweek2Array(1) = biweek2
    sitenames(1) = "Clear Lake Cabin"
    parkurls(1) = url

    parkid = "75099";
    url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid
    //System.out.println(url)
    doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get()
    biweek = doc.select("td[class^=status")
    doc2 = Jsoup.connect("http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.plusDays(14).toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid).userAgent("Mozilla/5.0").timeout(5000).get()
    biweek2 = doc2.select("td[class^=status")
    biweekArray(2) = biweek
    biweek2Array(2) = biweek2
    sitenames(2) = "Flag Point Lookout"
    parkurls(2) = url

    Ok(views.html.recreation(date, sitenames, parkurls, biweekArray, biweek2Array))
// 		Ok(views.html.recreation(doc.select("td[class^=status").toString))
// 		Ok(views.html.recreation(doc.select("td[class=status r]").toString))
 	}

}
