package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.rest.api.CrawlStatus
import com.tsykul.crawler.worker.domain.UrlStatus._
import com.tsykul.crawler.worker.domain.{CrawlRuntimeInfo, UrlInfo}
import com.tsykul.crawler.worker.messages._

class CrawlRootActor extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case crawlConfig@CrawlDefinition(seeds, filters, depth, crawlUid) =>
      log.info(s"Initialized crawl root, uid: $crawlUid")
      context.become(handleStats(Nil))
      for (seed <- seeds) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters))
        urlHandlerActor !
          Url(UrlInfo(seed, depth), Pending, CrawlRuntimeInfo(urlHandlerActor, self, crawlUid))
      }
    case msg: Any => unhandled(msg)
  }

  def handleStats(fetchedUrls: List[String]): Receive = {
    case fetchedUrl@Url(UrlInfo(url, _, _), Fetched, _) =>
      log.debug(s"Got a fetched url $url")
      context.become(handleStats(url :: fetchedUrls))
    case status: GetCrawlStatus =>
      log.info("Replying to stats request")
      sender ! CrawlStatus(fetchedUrls)
    case msg: Any => unhandled(msg)
  }
}
