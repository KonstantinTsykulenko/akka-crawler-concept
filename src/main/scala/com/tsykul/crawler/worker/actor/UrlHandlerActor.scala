package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.worker.messages.{CrawlRuntimeInfo, ParsedUrl, Url}

class UrlHandlerActor(val filters: List[String]) extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case url: Url =>
      fetcher ! url
    case parsedUrl@ParsedUrl(url, Url(link, rank, _, CrawlRuntimeInfo(root, stats, uid))) =>
      stats ! parsedUrl
      if (needsAnotherRound(rank) && isAllowed(url)) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters))
        urlHandlerActor ! Url(url, rank - 1, Option(link), CrawlRuntimeInfo(self, stats, uid))
      }
    case msg: Any => unhandled(msg)
  }

  private def isAllowed(url: String) = {
    filters.map(url.matches).reduce(_ || _)
  }

  private def needsAnotherRound(rank: Int) = {
    rank > 0
  }
}
