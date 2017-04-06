package net.coconauts.scalarest.queues

import com.typesafe.config.ConfigFactory
import net.coconauts.scalarest.Global
import org.specs2.mutable.Specification

import scala.util.Random

class RedisQueueIntegrationTest extends Specification {

  val conf = ConfigFactory.load();

  val queue = Global.queue

  "RedisQueue" should {

    "push and pop return the same result on the queue" in {

      val value = Random.nextString(5)
      val queueName = Random.nextString(5)

      queue.push(queueName, value)

      val result = queue.pop(queueName)

      result must beSome(value)

    }

    "Get status from queue" in {

      val result = queue.status()

      result must beSome[String]

      result must beSome("PONG")
    }

    "should list and remove all elements from list with popList " in {

      val value = Random.nextString(5)
      val value2 = Random.nextString(5)
      val queueName = Random.nextString(5)

      queue.push(queueName, value)
      queue.push(queueName, value2)

      val list = queue.popList(queueName)
      list must have size (2)
      list.head === value //First element inserted

      val listEmpty = queue.popList(queueName)
      listEmpty must be empty
    }

  }
}