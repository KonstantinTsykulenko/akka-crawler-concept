package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.tsykul.crawler.worker.domain.UrlStatus._
import com.tsykul.crawler.worker.domain.{UrlStatus, UrlInfo, CrawlRuntimeInfo}
import com.tsykul.crawler.worker.messages.Url

class UrlHandlerActor(val filters: List[String]) extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case url@Url(urlInfo, Pending, runtimeInfo) =>
      log.debug(s"Handling a url: $url")
      fetcher ! Url(urlInfo, Approved, runtimeInfo)
    case parsedUrl@Url(info@UrlInfo(url, rank, _), Parsed, runtimeInfo) =>
      if (needsAnotherRound(rank) && isAllowed(url)) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters))
        urlHandlerActor ! Url(info, Pending, runtimeInfo)
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
