package net.coconauts.scalarest.queues

import com.redis.RedisClientPool
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer

/**
  * Implementation of the Redis queue system
  **/
class RedisQueue(redis: RedisClientPool, prefix: String = "q") extends Queue {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def pop(queue: String) = redis.withClient {
    _.rpop(s"$prefix:$queue")
  }

  override def popList(queue: String) = redis.withClient { r =>

    val list = ArrayBuffer[String]()
    var elem = r.rpop(s"$prefix:$queue")

    while (elem.isDefined) {
      list += elem.get //Append operation
      elem = r.rpop(s"$prefix:$queue")
    }

    list.toSeq

  }

  override def push(queue: String, message: String) = redis.withClient {
    _.lpush(s"$prefix:$queue", message)
  }

  override def status() = redis.withClient {
    _.ping
  }

  override def register(name: String): Unit = {
    logger.info(s"Registering queue in Redis with $name")
    redis.withClient {
      _.hset("q:list", s"$prefix:$name", "")
    }
  }
}
