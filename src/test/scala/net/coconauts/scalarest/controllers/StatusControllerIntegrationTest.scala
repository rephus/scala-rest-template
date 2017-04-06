package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.PostgresTest
import org.specs2.mutable._
import spray.testkit.Specs2RouteTest
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import net.coconauts.scalarest.models.UserJsonProtocol._

//http://blog.scalac.io/2015/03/27/specs2-notes.html
class StatusControllerIntegrationTest extends Specification with Specs2RouteTest with StatusController with PostgresTest {
  def actorRefFactory = system


  "Status service" should {

    "return status on root" in {

      Get("/") ~> statusRoutes ~> check {
        val expected = Map("status" -> "ok",
          "environment" -> "test",
          "name" -> "scalarest",
          //"db" -> "ok",
          "queue" -> "ok")

        responseAs[Map[String, String]] === expected
      }

    }

    "return pong" in {

      print("PathEnd" + PathEnd)

      Get("/ping/") ~> statusRoutes ~> check {
        responseAs[String] === "pong"
      }

    }
  }
}
