
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
case class Subscription(phone: String, parkid: Int, id: Option[Int] = None)

class Subscriptions(tag: Tag) extends Table[Subscription](tag, "SUBSCRIPTIONS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def phone = column[String]("PHONE", O.NotNull)
  def parkid = column[Int]("PARKID", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (phone, parkid, id.?) <> (Subscription.tupled, Subscription.unapply)
}

object Subscriptions {
	val db = play.api.db.slick.DB
	val subscriptions = TableQuery[Subscriptions]
	def all: List[Subscription] = db.withSession { implicit session =>
    subscriptions.sortBy(_.id.asc.nullsFirst).list
	}
	def create(newsubscription: Subscription) = db.withTransaction{ implicit session =>
    subscriptions += newsubscription
	}
  def find(subscription: Subscription): Option[Subscription] = db.withSession { implicit session =>
    subscriptions.filter(a=> a.parkid === subscription.parkid && a.phone === subscription.phone).firstOption
  }
	def find(id: Int): Subscription = db.withSession{ implicit session =>
    subscriptions.filter(_.id === id).first
	}
  def findlist(parkid: Int): List[Subscription] = db.withSession{ implicit session =>
    subscriptions.filter(_.parkid === parkid).list
  }
	def update(updateSubscription: Subscription) = db.withTransaction{ implicit session =>
    subscriptions.filter(_.id === updateSubscription.id).update(updateSubscription)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
    subscriptions.filter(_.id === id).delete
	}
}