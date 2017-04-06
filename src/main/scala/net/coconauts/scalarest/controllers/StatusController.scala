package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.Global
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._
import spray.routing._
import org.slf4j.LoggerFactory

import scala.slick.driver.JdbcDriver.simple._

trait StatusController extends HttpService {
  private val logger = LoggerFactory.getLogger(this.getClass)

  val okStatus = "ok"

  def dbStatus() = {
    try {
      implicit val s = Global.db.createSession()
      //Users.objects.list.size // If database is not avaialble this should throw an error
      //Note: Be careful with this function, as it might cause unnecessary memory allocations, or database connections
      okStatus
    } catch {
      case e: Exception => "Error on database: " + e
    }
  }

  def queueStatus() = {
    try {
      Global.queue.status() match {
        case Some("PONG") => okStatus
        case _ => "No connection to queue"
      }
    } catch {
      case e: Exception => "Error on requesting queue status: " + e
    }
  }

  val statusRoutes =
    path(PathEnd) {
      get {

        val response = Map("status" -> okStatus,
          "environment" -> Global.conf.getString("env"),
          "name" -> Global.conf.getString("service"),
          //"db" -> dbStatus(),
          "queue" -> queueStatus())

        complete(response)
      }
    } ~ path("ping" /) {
      get {
        complete("pong")
      }

    }
}
