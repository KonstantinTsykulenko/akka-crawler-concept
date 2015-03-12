package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.rest.api.CrawlStatusReport
import com.tsykul.crawler.worker.domain.CrawlStatus._
import com.tsykul.crawler.worker.domain.UrlStatus._
import com.tsykul.crawler.worker.domain.{CrawlStatus, CrawlRuntimeInfo, UrlInfo}
import com.tsykul.crawler.worker.messages._

class CrawlRootActor extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case crawlConfig@CrawlDefinition(seeds, filters, depth, crawlUid) =>
      log.info(s"Initialized crawl root, uid: $crawlUid")
      for (seed <- seeds) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters, self))
        urlHandlerActor !
          Url(UrlInfo(seed, depth), Pending, CrawlRuntimeInfo(urlHandlerActor, self, crawlUid))
      }
      context.become(handleStats(Nil, Running, 0, seeds.length))
    case msg: Any => unhandled(msg)
  }

  def handleStats(fetchedUrls: List[String],
                  crawlStatus: CrawlStatus.Value, processedSeeds: Int, totalSeeds: Int): Receive = {
    case fetchedUrl@Url(UrlInfo(url, _, _), Fetched, _) =>
      log.debug(s"Got a fetched url $url")
      context.become(handleStats(url :: fetchedUrls, crawlStatus, processedSeeds, totalSeeds))
    case UrlProcessed =>
      log.info(s"Seed url fully processed")
      val newProcessedSeeds = processedSeeds + 1
      if (newProcessedSeeds == totalSeeds) {
        context.become(handleStats(fetchedUrls, Completed, newProcessedSeeds, totalSeeds))
      }
      context.become(handleStats(fetchedUrls, crawlStatus, newProcessedSeeds, totalSeeds))
    case status: GetCrawlStatus =>
      log.info("Replying to stats request")
      sender ! CrawlStatusReport(fetchedUrls, crawlStatus.toString)
    case msg: Any => unhandled(msg)
  }
}
