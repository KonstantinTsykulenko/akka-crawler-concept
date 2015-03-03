package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.worker.messages._

class CrawlRootActor(val crawlUid: String) extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case crawlConfig@CrawlConfig(seeds, filters, depth) =>
      log.info(s"Initialized crawl root, uid: $crawlUid")
      context.become(handleStats(Nil, Nil))
      for (seed <- seeds) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters))
        urlHandlerActor !
          Url(seed, depth, runtimeInfo = CrawlRuntimeInfo(urlHandlerActor, self, crawlUid))
      }
    case msg: Any => unhandled(msg)
  }

  def handleStats(parsedUrls: List[String], fetchedUrls: List[String]): Receive =
  {
    case parsedUrl@ParsedUrl(url, _) =>
      log.debug(s"Got a parsed url $url")
      context.become(handleStats(url :: parsedUrls, fetchedUrls))
    case fetchedUrl@FetchedUrl(_, Url(url, _, _, _)) =>
      log.debug(s"Got a fetched url $url")
      context.become(handleStats(parsedUrls, url :: fetchedUrls))
    case status: CrawlStatus =>
      log.info("Replying to stats request")
      sender ! CrawlStatusResponse(parsedUrls, fetchedUrls)
    case msg: Any => unhandled(msg)
  }
}
