package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.worker.messages.{CrawlConfig, ParsedUrl, Url}

class CrawlRootActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case crawlConfig@CrawlConfig(seeds, filters, depth) =>
      for (seed <- seeds) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters)) ! Url(seed, depth)
      }
    case parsedUrl@ParsedUrl(url, _) =>
      log.debug(s"parsed a url $url")
    case msg: Any => unhandled(msg)
  }
}
