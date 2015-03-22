package com.tsykul.crawler.test.backend.actor

import akka.actor.{Actor, ActorRef, Props}
import com.tsykul.crawler.test.backend.messages.Work

class WorkerMaster(dispatcher: ActorRef) extends Actor {
  override def receive: Receive = {
    case work: Work => context.actorOf(Props(classOf[Worker], sender, dispatcher)) forward work
  }
}
