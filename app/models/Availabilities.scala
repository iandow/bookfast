
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
case class Availability(name: String, date: String, parkid: Int, id: Option[Int] = None)

class Availabilities(tag: Tag) extends Table[Availability](tag, "AVAILABILITIES") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  def date = column[String]("DATE", O.NotNull)
  def parkid = column[Int]("PARKID", O.NotNull)
  
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (name, date, parkid, id.?) <> (Availability.tupled, Availability.unapply)
}

object Availabilities {
	val db = play.api.db.slick.DB
	val availability = TableQuery[Availabilities]
	def all: List[Availability] = db.withSession { implicit session =>
		availability.sortBy(_.date.asc.nullsFirst).list
	}
	def create(newavailability: Availability) = db.withTransaction{ implicit session =>
		availability += newavailability
	}
	def find(parkid: Int): Availability = db.withSession{ implicit session =>
		availability.filter(_.parkid === parkid).first
	}
  def find(date: String): Option[Availability] = db.withSession{ implicit session =>
    availability.filter(_.date === date).firstOption
	}
  def find(date: String, parkid: Int): Option[Availability] = db.withSession { implicit session =>
    availability.filter(a => a.date === date && a.parkid === parkid).firstOption
  }
	def update(updateAvailability: Availability) = db.withTransaction{ implicit session =>
		availability.filter(_.id === updateAvailability.id).update(updateAvailability)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
		availability.filter(_.id === id).delete
	}
  def delete(date: String, parkid: Int) = db.withSession { implicit session =>
      availability.filter(a => a.date === date && a.parkid === parkid).delete
  }
}