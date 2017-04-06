package net.coconauts.scalarest.queues

import akka.actor.{Actor, ActorSystem}
import akka.testkit.TestActorRef
import org.specs2.mock._
import org.specs2.mutable.Specification

class QueueListenerTest extends Specification with Mockito {

  implicit val system = ActorSystem("queue-listener-test")

  val queue = "queue"
  val mockQueue = mock[Queue]

  class SimpleActor extends Actor {
    def receive = {
      case QueueMessage(m, _) => println("Got message " + m)
    }
  }

  val simpleActor = TestActorRef(new SimpleActor)

  val listener = new QueueListener(mockQueue, queue) {
    override val queueProcessor = simpleActor
  }

  "Queue Listener" should {

    "Does not send messages to actor if nothing is in queue" in {
      mockQueue.pop(queue) returns None
      listener.listen
      true
    }

    "Send messages to actor when queue has messages" in {
      mockQueue.pop(queue) returns Some("test")
      listener.listen
      true
    }

  }

}
