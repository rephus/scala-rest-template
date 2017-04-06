package net.coconauts.scalarest.queues

/**
  * Interface for the queue system, every queue we use need to implement these functions.
  * It can be redis, rabbitmq, amazon sqs, a sql database...
  */
trait Queue {

  def pop(queue: String): Option[String]

  def popList(queue: String): Seq[String]

  def push(queue: String, message: String): Unit

  def status(): Option[String]

  def register(name: String): Unit

}
