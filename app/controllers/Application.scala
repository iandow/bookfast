
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
import models.{Site, Sites, Availabilities, Subscriptions}

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

  def showsites = Action {
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
 	}

  def showsite(parkid: Int) = Action {
      var site = Sites.find(parkid)

      val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")
      val today = Calendar.getInstance().getTime()
      val format1 = new SimpleDateFormat("MM/dd/yyyy");
      val date2 = format1.format(today)
      val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
      var i = 0
      val sitenames = new Array[String](1)
      val parkurls = new Array[String](1)
      val biweekArray = new Array[Elements](1)
      val biweek2Array = new Array[Elements](1)


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

        biweekArray(0) = biweek
        biweek2Array(0) = biweek2
        sitenames(0) = site.name
        parkurls(0) = url


      Ok(views.html.recreation(date, sitenames, parkurls, biweekArray, biweek2Array))
  // 		Ok(views.html.recreation(doc.select("td[class^=status").toString))
  // 		Ok(views.html.recreation(doc.select("td[class=status r]").toString))
   	}

}
