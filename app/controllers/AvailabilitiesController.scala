
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
import models.Availability
import models.Availabilities


object AvailabilitiesController extends Controller {
	val availabilityForm = Form(
	    mapping(
	        "name" -> text,
          "date" -> text,
          "parkid" -> number,
	        "id" -> optional(number)
	)(Availability.apply)(Availability.unapply))
  	def index = Action {
		  Ok(views.html.availabilities.index(Availabilities.all))
	}
	def show(id:Int) = Action {
		Ok(views.html.availabilities.show(Availabilities.find(id)))
	}
	def add = Action {

	    Ok(views.html.availabilities.add(availabilityForm))
	}
	def save = Action{implicit request =>
		val availability = availabilityForm.bindFromRequest.get
    Availabilities.create(availability)
		Redirect(routes.Application.availindex)
	}
	def delete(id: Int) = Action {implicit request =>
    Availabilities.delete(id)
		Redirect(routes.Application.availindex)
	}
}
