package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.worker.messages.{ParsedUrl, Url}

class UrlHandlerActor(val filters: List[String]) extends Actor with ActorLogging {

  val parser = context.actorOf(Props[ParserActor])
  val fetcher = context.actorOf(Props(classOf[FetcherActor], parser))

  override def receive: Receive = {
    case url: Url =>
      fetcher ! url
    case parsedUrl@ParsedUrl(url, Url(link, rank, origin)) =>
      context.parent ! parsedUrl
      if (needsAnotherRound(rank) && isAllowed(url)) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters)) ! Url(url, rank - 1, Option(link))
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
