package controllers

import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import models.Subscription
import models.Subscriptions


object SubscriptionsController extends Controller {
	val subscriptionForm = Form(
	    mapping(
	        "phone" -> text,
          "date" -> text,
          "parkid" -> number,
	        "id" -> optional(number)
	)(Subscription.apply)(Subscription.unapply))
  	def index = Action {
		  Ok(views.html.subscriptions.index(Subscriptions.all))
	}
	def show(id:Int) = Action {
		Ok(views.html.subscriptions.show(Subscriptions.find(id)))
	}
	def add(parkId:Int,name:String) = Action {
	    Ok(views.html.subscriptions.add(name,subscriptionForm.fill(Subscription("","*",parkId))))
	}
	def save = Action{implicit request =>
		val subscription = subscriptionForm.bindFromRequest.get
    Subscriptions.create(subscription)
		Redirect(routes.Application.subscriptionindex)
	}
	def delete(id: Int) = Action {implicit request =>
    Subscriptions.delete(id)
		Redirect(routes.Application.subscriptionindex)
	}
}
