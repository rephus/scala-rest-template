package net.coconauts.scalarest.queues

import akka.actor._
import org.slf4j.LoggerFactory

/**
  * Fetch messages from our queue system (Redis) and process the string on an akka actor
  * The listen process runs on an new infinite thread (not akka), when the service starts.
  *
  * @param queueService Instance of the service
  * @param queueName    Service queue to fetch message from (q:service)
  * @param interval     Interval for fetching messages (default 1 second)
  */
class QueueListener(queueService: Queue, queueName: String, interval: Int = 1000) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val queueProcessor = ActorSystem("queueProcessor").actorOf(Props[QueueProcessor])

  def listen = queueService.pop(queueName).foreach(
    message => {
      logger.debug(s"Got message from queue $queueName: $message")
      queueProcessor ! QueueMessage(message, queueName)
    }
  )

  val thread = new Thread {
    override def run {

      logger.info(s"Listening to messages on queue $queueName")

      while (true) {
        listen
        Thread.sleep(interval)
      }
    }
  }
  thread.start
}
