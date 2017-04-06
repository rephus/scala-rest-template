package net.coconauts.scalarest.queues

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import net.coconauts.scalarest.{Global, PostgresTest, Utils}
import org.specs2.mutable.Specification


class QueueProcessorTest extends Specification with PostgresTest {

  val processor = ActorSystem("queueProcessor").actorOf(Props[QueueProcessor])
  val conf = ConfigFactory.load()

  "should store in q:failed all failed messages" in {

    val queue = Global.queue

    val message = """{"foo":"bar"}"""
    val queueMessage = QueueMessage(message, "")

    processor ! queueMessage
    Utils.retry(10) {
      val list = queue.popList("q:failed")
      println("List of messages failed " + list)

      QueueMessage(list.head, "")

      list.size must be greaterThan (0)
    }

  }

  "should store in q:completed all completed messages " in {

    val queue = Global.queue

    val message = """ {"message_type": "test_type", "from": "test"} """

    val queueMessage = QueueMessage(message, "")

    processor ! queueMessage

    Utils.retry(10) {
      val list = queue.popList("q:completed")
      println("List of messages failed " + list)

      list.size must be greaterThan (0)
    }
  }


}
