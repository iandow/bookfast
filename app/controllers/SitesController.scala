
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
import models.Site
import models.Sites


object SitesController extends Controller {
	val siteForm = Form(
	    mapping(
	        "name" -> text,
	        "id" -> optional(number),
          "parkid" -> number
	)(Site.apply)(Site.unapply))
  	def index = Action {
		  Ok(views.html.sites.index(Sites.all))
	}
	def show(id:Int) = Action {
		Ok(views.html.sites.show(Sites.find(id)))
	}
	def add = Action {
	    Ok(views.html.sites.add(siteForm))
	}
	def save = Action{implicit request =>
		val site = siteForm.bindFromRequest.get
    Sites.create(site)
		Redirect(routes.Application.index)
	}
	def edit(id:Int) = Action {
		Ok(views.html.sites.edit(id, siteForm.fill(Sites.find(id))))
	}
	def update(updateid: Int) = Action {implicit request =>
		val site = siteForm.bindFromRequest.get
		val newsite = site.copy(id = Some(updateid))
    Sites.update(newsite)
		Redirect(routes.Application.index)
	}
	def delete(id: Int) = Action {implicit request =>
    Sites.delete(id)
		Redirect(routes.Application.index)
	}

}
