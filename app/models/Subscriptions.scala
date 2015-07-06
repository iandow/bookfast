package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import play.api.data.Forms._
case class Subscription(phone: String, date: String, parkid: Int, id: Option[Int] = None)

class Subscriptions(tag: Tag) extends Table[Subscription](tag, "SUBSCRIPTIONS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def phone = column[String]("PHONE", O.NotNull)
  def date = column[String]("DATE")
  def parkid = column[Int]("PARKID", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (phone, date, parkid, id.?) <> (Subscription.tupled, Subscription.unapply)
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
	def find(phone: Int): Subscription = db.withSession{ implicit session =>
    subscriptions.filter(_.id === phone).first
	}
	def update(updateSubscription: Subscription) = db.withTransaction{ implicit session =>
    subscriptions.filter(_.id === updateSubscription.id).update(updateSubscription)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
    subscriptions.filter(_.id === id).delete
	}
}