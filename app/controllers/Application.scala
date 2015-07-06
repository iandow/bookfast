package controllers

import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
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
}
