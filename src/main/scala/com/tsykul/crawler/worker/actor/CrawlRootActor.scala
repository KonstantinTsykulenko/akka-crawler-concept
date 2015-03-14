package com.tsykul.crawler.worker.actor

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig
import com.tsykul.crawler.rest.api.CrawlStatusReport
import com.tsykul.crawler.worker.messages._

class CrawlRootActor(val dispatcher: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case crawlConfig@CrawlDefinition(seeds, filters, depth, crawlUid) =>
      log.debug(s"Initialized crawl root, uid: $crawlUid")
      for (seed <- seeds) {
        dispatcher ! ConsistentHashableEnvelope(SeedUrl(Url(seed, depth), self, filters), seed)
      }
      context.become(aggregate(seeds.length, 0))
    case msg: Any => unhandled(msg)
  }

  def aggregate(totalSeeds: Int, totalProcessed: Int): Receive = {
    case status: GetCrawlStatus =>
      //TODO change to proper enum
      log.debug("Replying to stats request")
      val status = if (totalProcessed == totalSeeds) {
        "FINISHED"
      } else {
        "RUNNING"
      }
      sender ! CrawlStatusReport(Nil, status)
    case url: UrlProcessed =>
      context.become(aggregate(totalSeeds, totalProcessed + 1))
    case msg: Any =>
      unhandled(msg)
  }
}