package net.coconauts.scalarest.queues

import akka.actor.Actor
import net.coconauts.scalarest.Global
import org.slf4j.LoggerFactory

/**
  * Process and parse messages provided in this actor
  */
class QueueProcessor extends Actor {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def receive = {

    case QueueMessage(message, _) => try {

      logger.info(s"Preprocessing message: $message")
      QueueMessage.parseJson(message) match {

        case Some(ParsedQueueMessage("message_type", from)) =>
          logger.info(s"Got message type 'message_type' message: $from")

        case _ => logger.debug(s"Skipping message $message")
      }

      Global.queue.push("q:completed", message)

    } catch {
      case e: Exception =>

        logger.error("Unable process message " + message, e)

        Global.queue.push("q:failed", message)
    }
  }
}