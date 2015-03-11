package com.tsykul.crawler.rest.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import com.tsykul.crawler.rest.api.CrawlStatus
import com.tsykul.crawler.worker.messages.GetCrawlStatus

class CrawlStatusResultHandler(val worker: ActorRef) extends Actor with ActorLogging{
  var origSender: ActorRef = null

  override def receive: Receive = {
    case ConsistentHashableEnvelope(GetCrawlStatus(uid, origin), key) =>
      origSender = sender()
      worker ! ConsistentHashableEnvelope(GetCrawlStatus(uid, self), key)
    case response: CrawlStatus =>
      origSender ! response
      self ! PoisonPill
  }
}
