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
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
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

  // Setup Twilio
  val sid = Play.current.configuration.getString("twilio_sid").getOrElse("")
  val token = Play.current.configuration.getString("twilio_token").getOrElse("")
  val from = Play.current.configuration.getString("twilio_number").getOrElse("")
  val client = new TwilioRestClient(sid, token)
  val params2 = new util.ArrayList[NameValuePair]()
  params2.add(new BasicNameValuePair("From", from));
  val messageFactory: SmsFactory = client.getAccount.getSmsFactory

  // Setup twitter
  val cb = new ConfigurationBuilder()
  cb.setDebugEnabled(true)
    .setOAuthConsumerKey(Play.current.configuration.getString("twitter_ConsumerKey").getOrElse(""))
    .setOAuthConsumerSecret(Play.current.configuration.getString("twitter_ConsumerSecret").getOrElse(""))
    .setOAuthAccessToken(Play.current.configuration.getString("twitter_AccessToken").getOrElse(""))
    .setOAuthAccessTokenSecret(Play.current.configuration.getString("twitter_AccessTokenSecret").getOrElse(""))
  val tf = new TwitterFactory(cb.build())
  val twitter = tf.getInstance()

  def receive = {
    case DateToCheck(who) => greeting = s"checking date:, $who"
    case Greet => sender ! Greeting(greeting) // Send the current greeting back to the sender
      Logger.info(greeting)

      var sites = Sites.all

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

        // Stop keeping track of dates that are earlier than today + 6 months
        var avails = Availabilities.all
        for (x <- avails) {
          if (formatter.parseDateTime(x.date).isBefore(date.getMillis())) {
            Availabilities.delete(x.date, x.parkid)
          }
        }

        // keep state on availabilities
        for ((day, i) <- biweek.asScala.zipWithIndex) {
          if (day.text() == "A" || day.text() == "a") {
            val size = Availabilities.all.size
            val d = date.plusDays(i).toString("MM/dd/yyyy")
            val record = Availabilities.find(d, site.parkid)

            if (size == 0 || record == None) {
              val a = Availability(site.name, d, site.parkid)
              Availabilities.create(a)

              // Send SMS message to subscribers
              val msg = site.name + " is available for " + d
              val subscribers = Subscriptions.findlist(site.parkid)
              params2.add(new BasicNameValuePair("Body", msg));
              for (subscriber <- subscribers) {
                Logger.info("Texting SMS to " + subscriber.phone)
                params2.add(new BasicNameValuePair("To", subscriber.phone));
                messageFactory.create(params2)
              }
              // Send tweet
              val status = twitter.updateStatus(msg + " " + url)
              Logger.info("Tweeting: " + status.getText());
            }

          } else if (day.text() != "A" && day.text() != "a") {

            val d = date.plusDays(i).toString("MM/dd/yyyy")
            val record = Availabilities.find(d, site.parkid.toInt)
            if (record != None) {
              Availabilities.delete(d, site.parkid.toInt)

              var msg = site.name + " is not longer available for " + d
              // Send SMS message to subscribers
              if (day.text() == "R") {
                msg = site.name + " has been reserved for " + d
              }
              val subscribers = Subscriptions.findlist(site.parkid)
              params2.add(new BasicNameValuePair("Body", msg));
              for (subscriber <- subscribers) {
                Logger.info("Sending SMS: " + msg)
                params2.add(new BasicNameValuePair("To", subscriber.phone));
                messageFactory.create(params2)
              }
              // Send tweet
              val status = twitter.updateStatus(msg)
              Logger.info("Tweeted: " + status.getText());
            }
          }
        }
      }
  }
}
