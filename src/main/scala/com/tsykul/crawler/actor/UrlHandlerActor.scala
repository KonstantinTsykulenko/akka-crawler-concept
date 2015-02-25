package com.tsykul.crawler.actor

import akka.actor.{ActorLogging, Actor, Props}
import com.tsykul.crawler.messages.Url
import com.tsykul.crawler.messages.ParsedUrl

class UrlHandlerActor(val depth: Int) extends Actor with ActorLogging {

  val parser = context.actorOf(Props[ParserActor])
  val fetcher = context.actorOf(Props(classOf[FetcherActor], parser))

  override def receive: Receive = {
    case url: Url => fetcher ! url
    case ParsedUrl(url) => {
      if (depth > 0) {
        log.info(s"recursive fetching, depth ${depth}")
        context.actorOf(Props(classOf[UrlHandlerActor], depth - 1)) ! Url(url)
      }
      else {
        log.info("max depth reached")
      }
    }
    case msg: Any => unhandled(msg)
  }
}
