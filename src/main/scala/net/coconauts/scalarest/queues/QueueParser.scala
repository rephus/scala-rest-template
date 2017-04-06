package net.coconauts.scalarest.queues

import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol._
import spray.json._


case class QueueMessage(message: String, queue: String)

case class ParsedQueueMessage(messageType: String, from: String)

object QueueMessage {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def parseString(json: JsValue): String = {
    json match {
      case JsNumber(n) => n.toString
      case JsString(s) => s
      case JsNull => ""
      case x => logger.warn(s"Json string field in $json not expected: $x ")
        ""
    }
  }

  /**
    * Util function to parse map of values from string
    *
    * @param params "param1=value1, param2=value2"
    * @return Map[String, String]
    */
  def parseParams(params: String): Map[String, String] = {
    params.split(",").map(s => {
      val arg = s.split("=")
      (arg.head.trim -> arg.last.trim)
    }).toMap[String, String]
  }

  def getString(jsValue: Map[String, JsValue], field: String): String = {
    // Method toString returns the string "between quotes", we want converTo[String] here
    jsValue.get(field) match {
      case Some(JsString(s)) => s
      case x => throw new DeserializationException(s"Expected String field on $field in $jsValue but got $x ")
    }
  }

  def getInt(jsValue: Map[String, JsValue], field: String): Int = {

    jsValue.get(field) match {
      case Some(JsNumber(s)) => s.toIntExact
      case x => throw new DeserializationException(s"Expected Int field on $field in $jsValue but got $x ")
    }
  }

  def getJson(jsValue: Map[String, JsValue], field: String, default: Option[Map[String, JsValue]] = None): Map[String, JsValue] = {
    jsValue.get(field) match {
      case Some(JsObject(s)) => jsValue.get(field).get.asJsObject.fields
      case None if !default.isEmpty => default.get
      case x => throw new DeserializationException(s"Expected JsObject field on $field in $jsValue but got $x ")
    }
  }

  /**
    * Parse message into JSON with format
    *
    * @param message { "type": "x", "from": "server" }
    * @return If parsing is successfull, it will return a Some[QueueMessageDetails], otherwise will throw a Exception
    */
  def parseJson(message: String): Option[ParsedQueueMessage] = {

    /* For more straightforward messages, can also do
        JsonParser(message).asJsObject.getFields("type", "from") match {
          case Seq(JsString(messageType), JsString(from))
     */
    val json = message.parseJson.asJsObject.fields

    val messageType = getString(json, "message_type")

    messageType match {
      //It can be any message_type, in the future, we can try to parse different messages depending on the type
      case mtype => Some(parseQueueMessage(json))
      //case _ => None
    }
  }


  def parseQueueMessage(json: Map[String, JsValue]): ParsedQueueMessage = {
    val messageType = getString(json, "message_type")
    val from = getString(json, "from") //can't be null

    ParsedQueueMessage(
      messageType = messageType,
      from = from
    )
  }
}