package controllers

import java.text.SimpleDateFormat
import java.util
import java.util.Calendar
import play.api.Play
import akka.actor.Actor
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import play.api.Logger
import scala.collection.JavaConverters._
import models.{Subscriptions, Sites, Availability, Availabilities}
import com.twilio.sdk.TwilioRestClient
import com.twilio.sdk.resource.factory.SmsFactory
import com.twilio.sdk.resource.instance.Sms
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by iandow on 7/12/15.
 */
class Greeter extends Actor {
  var greeting = ""

  def receive = {
    case DateToCheck(who) => greeting = s"checking date:, $who"
    case Greet => sender ! Greeting(greeting) // Send the current greeting back to the sender
    Logger.info(greeting)

    var sites = Sites.all

    val sid = "ACe591ba06f233fcdc6f63143a75419fa7"
    val token = "4c804fa47f98f2e8f214235db20481c6"
    val to = "202-507-9070"
    val from = "503-278-4693"
    val client = new TwilioRestClient(sid, token)
    val params2 = new util.ArrayList[NameValuePair]()
    params2.add(new BasicNameValuePair("From", from));
    val messageFactory: SmsFactory = client.getAccount.getSmsFactory

    for(site <- sites) {

      val formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
      val today = Calendar.getInstance().getTime()
      //    val date = formatter.parseDateTime("1/18/2016")
      val format1 = new SimpleDateFormat("MM/dd/yyyy");
      val date2 = format1.format(today)
      val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
      val url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate=" + date.toString("MM/dd/yyyy") + "&contractCode=NRSO&parkId=" + site.parkid
      val doc = Jsoup.connect(url).userAgent("Mozilla/5.0")
        .timeout(Play.current.configuration.getInt("httptimeout").getOrElse(0)).get()
      val biweek = doc.select("td[class^=status")

      val doc2 = Jsoup.connect("http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate=" + date.plusDays(14).toString("MM/dd/yyyy") + "&contractCode=NRSO&parkId=" + site.parkid).userAgent("Mozilla/5.0").timeout(Play.current.configuration.getInt("httptimeout").getOrElse(0)).get()
      val biweek2 = doc2.select("td[class^=status")

      biweek.addAll(biweek2)

      for ((day, i) <- biweek.asScala.zipWithIndex) {
        if (day.text() == "A" || day.text() == "a") {
          val size = Availabilities.all.size
          val d = date.plusDays(i).toString("MM/dd/yyyy")
          val record = Availabilities.find(d, site.parkid)

          if (size == 0 || record == None) {
            val a = Availability(site.name, d, site.parkid)
            Availabilities.create(a)

            // Send SMS message thru Twilio to every subscriber to sites.parkid
            val msg = site.name + " is available for " + d
            val subscribers = Subscriptions.findlist(site.parkid)
            params2.add(new BasicNameValuePair("Body", msg));
            for (subscriber <- subscribers) {
              Logger.info("Sending SMS to " + subscriber.phone)
              params2.add(new BasicNameValuePair("To", subscriber.phone));
              messageFactory.create(params2)
            }
          }

        } else if (day.text() != "A" && day.text() != "a") {

          val d = date.plusDays(i).toString("MM/dd/yyyy")
          val record = Availabilities.find(d, site.parkid.toInt)
          if (record != None) {
            Availabilities.delete(d, site.parkid.toInt)

            // Send SMS message thru Twilio
            val msg = site.name + " has been reserved for " + d
            val subscribers = Subscriptions.findlist(site.parkid)
            params2.add(new BasicNameValuePair("Body", msg));
            for (subscriber <- subscribers) {
              Logger.info("Sending SMS: " + msg)
              params2.add(new BasicNameValuePair("To", subscriber.phone));
              messageFactory.create(params2)
            }

          }
        }
      }
    }
  }
}
