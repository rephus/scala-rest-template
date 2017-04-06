package net.coconauts.scalarest.models

import net.coconauts.scalarest.{Global, Utils}
import spray.json._

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted._
import scala.util.Random

case class User(id: Option[Int] = None, email: String = "")

class Users(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def email = column[String]("email")

  def * = (id.?, email) <>(User.tupled, User.unapply)
}

object Users {

  import scala.slick.driver.JdbcDriver.simple._

  lazy val objects = TableQuery[Users]

  def insert(user: User)(implicit session: Session): Int = {

    val inserting = objects returning objects.map(_.id)
    inserting += user
  }

  def get(id: Int): Option[User] = {
    implicit val s = Global.db.createSession()
    objects.filter(_.id === id).list.headOption
  }

  def getOrCreate(email: String)(implicit session: Session): User = {

    Users.objects.filter(_.email === email).list.headOption.getOrElse({
      val inserting = objects returning objects.map(_.id)
      val id = inserting += User(email = email)
      get(id).get
    })
  }

  /**
    * Creates a new random user, useful for tests
    */
  def random: User = {
    User(
      id = Some(Math.abs(Random.nextInt)),
      email = s"${Utils.randomString}@${Utils.randomString}.com"
    )
  }

}

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}

