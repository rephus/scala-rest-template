package net.coconauts.scalarest.models

import net.coconauts.scalarest.models.UserJsonProtocol._
import org.specs2.mutable.Specification
import spray.json._

class UserTest extends Specification {

  "User" should {

    "should transform to JSON" in {

      val user = Users.random

      val json = user.toJson

      val expected: Map[String, JsValue] = Map("id" -> JsNumber(user.id.get), "email" -> JsString(user.email))
      json === expected.toJson
    }

    "should parse from JSON" in {

      val user = Users.random

      val map: Map[String, JsValue] = Map("id" -> JsNumber(user.id.get), "email" -> JsString(user.email))
      val json = map.toJson

      UserJsonProtocol.userFormat.read(json) === user
    }

  }
}
