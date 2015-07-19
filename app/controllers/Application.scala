package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.JavaConversions._
import com.twilio.sdk.TwilioRestClient
import com.twilio.sdk.resource.factory.SmsFactory
import com.twilio.sdk.resource.instance.Sms
import com.twilio.sdk.client.TwilioCapability
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.factory.SmsFactory
import com.twilio.sdk.resource.instance.Sms
import play.api.Logger
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
    val formatter = DateTimeFormat.forPattern("MM/dd/yyyy")
    val today = Calendar.getInstance().getTime()
//    val date = formatter.parseDateTime("1/18/2016")
    val format1 = new SimpleDateFormat("MM/dd/yyyy");
    val date2 = format1.format(today)
    val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
    val url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid
    //System.out.println(url)
    val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get()

//    System.out.println(doc.body().toString());
    val biweek = doc.select("td[class^=status")

    val doc2 = Jsoup.connect("http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.plusDays(14).toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid).userAgent("Mozilla/5.0").timeout(5000).get()
    val biweek2 = doc2.select("td[class^=status")

//    var dailystatus = new Array[String](14)
//    for (element <- calendar_elements) {
//      dailystatus[i] = element.owntext()
//    }

    Logger.info("Sending SMS")
    val sid = "ACe591ba06f233fcdc6f63143a75419fa7"
    val token = "4c804fa47f98f2e8f214235db20481c6"
    val msg = "hello"
    val to = "202-507-9070"
    val from = "503-278-4693"
    val client = new TwilioRestClient(sid, token)
    val params= Map(("Body", msg), ("To", to), ("From", from))

    val messageFactory: SmsFactory = client.getAccount.getSmsFactory
    val message: Sms = messageFactory.create(params)
    System.out.println(message.getSid());



    Ok(views.html.recreation(date, biweek, biweek2))
// 		Ok(views.html.recreation(doc.select("td[class^=status").toString))
// 		Ok(views.html.recreation(doc.select("td[class=status r]").toString))
 	}

}
