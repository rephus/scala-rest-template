package net.coconauts.scalarest

import com.redis.{RedisClient, RedisClientPool}
import com.typesafe.config.{Config, ConfigFactory}
import net.coconauts.scalarest.queues.RedisQueue
import org.slf4j.LoggerFactory

import scala.slick.driver.PostgresDriver.simple._

object Global {

  private val logger = LoggerFactory.getLogger(this.getClass)

  // Load enviroment settings,
  // If none, it will load `application.conf` only
  private val env = System.getenv("MODE")
  var conf: Config = null
  try {
    conf = ConfigFactory.load(env + ".conf")
    logger.info(s"Loaded settings: ${conf.getString("env")}")

  } catch {
    case _: Throwable => {
      logger.info(s"Unable to load config $env, loading defaults")
      conf = ConfigFactory.load()
      logger.info(s"Loaded settings: ${conf.getString("env")}")
    }
  }
  logger.info(s"Loading queue from host " + conf.getString("redis.host"))

  val queue = {
    val redisHost = conf.getString("redis.host")
    val redisPort = conf.getInt("redis.port")
    val redisDatabase = conf.getInt("redis.database")

    val client = new RedisClientPool(redisHost, redisPort, database = redisDatabase)
    new RedisQueue(client)
  }

  logger.info(s"Loading db from host " + conf.getString("postgres.url"))

  val db = Database.forConfig("postgres", conf)

}
