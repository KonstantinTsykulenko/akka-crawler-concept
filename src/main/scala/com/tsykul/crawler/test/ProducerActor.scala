package com.tsykul.crawler.test

import akka.actor.{Actor, ActorRefFactory}

class ProducerActor extends Actor with WorkerRouter {
  override def receive: Receive = { case work: DoWork => router ! work }

  override def factory: ActorRefFactory = context
}
