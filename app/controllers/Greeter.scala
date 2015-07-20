package controllers

import java.text.SimpleDateFormat
import java.util
import java.util.Calendar

import akka.actor.Actor
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import play.api.Logger
import scala.collection.JavaConverters._
import models.{Availability, Availabilities}
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

      val parkid = "75098";
      val formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
      val today = Calendar.getInstance().getTime()
      //    val date = formatter.parseDateTime("1/18/2016")
      val format1 = new SimpleDateFormat("MM/dd/yyyy");
      val date2 = format1.format(today)
      val date = formatter.parseDateTime(date2.toString()).plusMonths(6).minusWeeks(1)
//      val date = formatter.parseDateTime("1/18/2016");
      val url = "http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate=" + date.toString("MM/dd/yyyy") + "&contractCode=NRSO&parkId=" + parkid
      //System.out.println(url)
      val doc = Jsoup.connect(url).userAgent("Mozilla/5.0")
        .timeout(5000).get()
      val biweek = doc.select("td[class^=status")

      val doc2 = Jsoup.connect("http://www.recreation.gov/campsiteCalendar.do?page=matrix&calarvdate="+date.plusDays(14).toString("MM/dd/yyyy")+"&contractCode=NRSO&parkId="+parkid).userAgent("Mozilla/5.0").timeout(5000).get()
      val biweek2 = doc2.select("td[class^=status")

      biweek.addAll(biweek2)

      val sid = "ACe591ba06f233fcdc6f63143a75419fa7"
      val token = "4c804fa47f98f2e8f214235db20481c6"
      val to = "202-507-9070"
      val from = "503-278-4693"
      val client = new TwilioRestClient(sid, token)
      val params2 = new util.ArrayList[NameValuePair]()
      params2.add(new BasicNameValuePair("To", to));
      params2.add(new BasicNameValuePair("From", from));

      for ((day,i) <- biweek.asScala.zipWithIndex) {
        if (day.text() == "A" || day.text() == "a") {
//          println("Available " + date.plusDays(i).toString("MM/dd/yyyy") + " " + parkid.toInt)

          val size = Availabilities.all.size
          val d = date.plusDays(i).toString("MM/dd/yyyy")
          val record = Availabilities.find(d,parkid.toInt)

          if (size == 0 || record == None) {
            val a = Availability("5milebutte", d, parkid.toInt)
            Availabilities.create(a)

            val msg = "Five Mile Butte is available for " + d
            Logger.info("Sending SMS: " + msg)
            params2.add(new BasicNameValuePair("Body", msg));
            val messageFactory: SmsFactory = client.getAccount.getSmsFactory
            val message: Sms = messageFactory.create(params2)
          }

//          println(Availabilities.find(date.plusDays(i).toString("MM/dd/yyyy"), parkid.toInt).toString)
//          }
        } else if (day.text() != "A" && day.text() != "a") {
          val d = date.plusDays(i).toString("MM/dd/yyyy")
          val record = Availabilities.find(d,parkid.toInt)
          if (record != None) {
            Availabilities.delete(d, parkid.toInt)

            val msg = "Five Mile Butte has been reserved for " + d
            Logger.info("Sending SMS: " + msg)
            params2.add(new BasicNameValuePair("Body", msg));
            val messageFactory: SmsFactory = client.getAccount.getSmsFactory
            val message: Sms = messageFactory.create(params2)
          }

        }


      }

  }
}
