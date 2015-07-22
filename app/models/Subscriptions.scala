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