package com.tsykul.crawler.test

import akka.actor.{ActorRefFactory, Props}
import akka.routing.ConsistentHashingPool

trait WorkerRouter {
  def factory: ActorRefFactory

  val router = factory.actorOf(ConsistentHashingPool(1).withHashMapper(new WorkerMapper).props(Props[WorkerActor]), "workerRouter")
}
