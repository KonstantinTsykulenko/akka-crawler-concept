package com.tsykul.crawler.actor

import akka.actor.{ActorLogging, Props, Actor}
import com.tsykul.crawler.messages.{CrawlConfig, ParsedUrl, Url}

class CrawlRootActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case crawlConfig@CrawlConfig(seeds, filters, depth) =>
      for (seed <- seeds) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters)) ! Url(seed, depth)
      }
    case parsedUrl@ParsedUrl(url, _) =>
      //log.info(s"parsed a url $url")
    case msg: Any => unhandled(msg)
  }
}
