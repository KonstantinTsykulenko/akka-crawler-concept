package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.rest.api.CrawlStatusResponse
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
          Url(seed, depth, runtimeInfo = CrawlRuntimeInfo(urlHandlerActor, self, crawlUid))
      }
    case msg: Any => unhandled(msg)
  }

  def handleStats(fetchedUrls: List[String]): Receive = {
    case fetchedUrl@FetchedUrl(_, Url(url, _, _, _)) =>
      log.debug(s"Got a fetched url $url")
      context.become(handleStats(url :: fetchedUrls))
    case status: CrawlStatus =>
      log.info("Replying to stats request")
      sender ! CrawlStatusResponse(fetchedUrls)
    case msg: Any => unhandled(msg)
  }
}
