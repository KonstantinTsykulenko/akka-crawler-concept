package com.tsykul.crawler.worker.actor

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.worker.messages.{CrawlRuntimeInfo, CrawlConfig, ParsedUrl, Url}

class CrawlRootActor(val crawlUid: String) extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case crawlConfig@CrawlConfig(seeds, filters, depth) =>
      for (seed <- seeds) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters))
        urlHandlerActor !
          Url(seed, depth, runtimeInfo = CrawlRuntimeInfo(urlHandlerActor, self, crawlUid))
      }
    case parsedUrl@ParsedUrl(url, _) =>
      log.debug(s"parsed a url $url")
    case msg: Any => unhandled(msg)
  }
}
