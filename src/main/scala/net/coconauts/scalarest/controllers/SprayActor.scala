package net.coconauts.scalarest.controllers

import akka.actor.Actor

class SprayActor extends Actor
  with UserController
  with StatusController {

  def actorRefFactory = context

  val routes = {
    userRoutes ~ statusRoutes
  }

  def receive = runRoute(routes)
}



