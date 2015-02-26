package com.tsykul.crawler.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.messages.{ParsedUrl, Url}

class UrlHandlerActor(val filters: List[String]) extends Actor with ActorLogging {

  val parser = context.actorOf(Props[ParserActor])
  val fetcher = context.actorOf(Props(classOf[FetcherActor], parser))

  override def receive: Receive = {
    case url@Url(link, rank, _) =>
      fetcher ! url
    case parsedUrl@ParsedUrl(url, Url(link, rank, origin)) =>
      log.debug(s"recursive fetching, rank $rank")
      context.parent ! parsedUrl
      if (needsAnotherRound(rank) && isAllowed(url))
        context.actorOf(Props(classOf[UrlHandlerActor], filters)) ! Url(url, rank - 1, Option(link))
    case msg: Any => unhandled(msg)
  }

  private def isAllowed(url: String) = {
    filters.map(url.matches).reduce(_ || _)
  }

  private def needsAnotherRound(rank: Int) = {
    rank > 0
  }
}
