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