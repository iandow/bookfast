
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
package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import play.api.data.Forms._
case class Site(name: String, id: Option[Int] = None, parkid: Int)

class Sites(tag: Tag) extends Table[Site](tag, "SITES") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  def parkid = column[Int]("PARKID", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (name, id.?, parkid) <> (Site.tupled, Site.unapply)
}

object Sites {
	val db = play.api.db.slick.DB
	val sites = TableQuery[Sites]
	def all: List[Site] = db.withSession { implicit session =>
		sites.sortBy(_.id.asc.nullsFirst).list
	}
	def create(newsite: Site) = db.withTransaction{ implicit session =>
		sites += newsite
	}
	def find(parkid: Int): Site = db.withSession{ implicit session =>
		sites.filter(_.parkid === parkid).first
	}
	def update(updateSite: Site) = db.withTransaction{ implicit session =>
		sites.filter(_.id === updateSite.id).update(updateSite)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
		sites.filter(_.id === id).delete
	}
}