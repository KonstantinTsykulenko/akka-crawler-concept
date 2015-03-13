package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.rest.api.CrawlStatusReport
import com.tsykul.crawler.worker.messages._

class CrawlRootActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case crawlConfig@CrawlDefinition(seeds, filters, depth, crawlUid) =>
      log.info(s"Initialized crawl root, uid: $crawlUid")
      for (seed <- seeds) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters, self, Url(seed, depth)))
      }
      context.become(aggregate(seeds.length, 0))
    case msg: Any => unhandled(msg)
  }

  def aggregate(totalSeeds: Int, totalProcessed: Int): Receive = {
    case status: GetCrawlStatus =>
      log.info("Replying to stats request")
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