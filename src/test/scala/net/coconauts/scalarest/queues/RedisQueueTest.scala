package net.coconauts.scalarest.queues

import com.redis.RedisClientPool
import org.specs2.mock._
import org.specs2.mutable.Specification

import scala.util.Random

class RedisQueueTest extends Specification with Mockito {

  val mockClient = mock[RedisClientPool]
  val queue = new RedisQueue(mockClient)


  "RedisQueue" should {

    "should write in queue with prefix q: " in {

      val value = Random.nextString(5)
      val queueName = Random.nextString(5)

      queue.push(queueName, value)

      //https://etorreborre.github.io/specs2/guide/SPECS2-3.0/org.specs2.guide.UseMockito.html
      there was one(mockClient).withClient(_.rpush(s"q:$queueName", value))
    }

  }
}
