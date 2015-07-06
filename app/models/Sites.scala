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
		sites.filter(_.id === parkid).first
	}
	def update(updateSite: Site) = db.withTransaction{ implicit session =>
		sites.filter(_.id === updateSite.id).update(updateSite)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
		sites.filter(_.id === id).delete
	}
}