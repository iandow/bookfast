
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

import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import models.{Subscriptions, Subscription}


object SubscriptionsController extends Controller {
	val subscriptionForm = Form(
	    mapping(
	        "phone" -> text,
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
	    Ok(views.html.subscriptions.add(name,subscriptionForm.fill(Subscription("",parkId))))
	}
	def save = Action{implicit request =>
		val subscription = subscriptionForm.bindFromRequest.get
    if (Subscriptions.find(subscription).toString() == "None")
      Subscriptions.create(subscription)
    else
      Subscriptions.update(subscription)
		Redirect(routes.Application.subscriptionindex)
	}
	def delete(id: Int) = Action {implicit request =>
    Subscriptions.delete(id)
		Redirect(routes.Application.subscriptionindex)
	}
}
