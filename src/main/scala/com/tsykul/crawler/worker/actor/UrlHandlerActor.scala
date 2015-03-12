package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.tsykul.crawler.worker.domain.UrlInfo
import com.tsykul.crawler.worker.domain.UrlStatus._
import com.tsykul.crawler.worker.messages.{ParsingEnded, Url, UrlProcessed}

class UrlHandlerActor(val filters: List[String], val root: ActorRef) extends Actor with ActorLogging {

  val fetcher = context.actorSelection("/user/fetchers")

  override def receive: Receive = {
    case url@Url(urlInfo@UrlInfo(_, rank, _), Pending, runtimeInfo) =>
      log.debug(s"Handling a url: $url")
      if (needsAnotherRound(rank)) {
        fetcher ! Url(urlInfo, Approved, runtimeInfo)
        context.become(gather(0, 0))
      } else {
        root ! UrlProcessed
        context.become(finished)
      }
    case msg: Any =>
      unhandled(msg)
  }

  def gather(pendingUrls: Int, fetchedUrls: Int): Receive = {
    case parsedUrl@Url(info@UrlInfo(url, _, _), Parsed, runtimeInfo) =>
      if (isAllowed(url)) {
        val urlHandlerActor = context.actorOf(Props(classOf[UrlHandlerActor], filters, self))
        urlHandlerActor ! Url(info, Pending, runtimeInfo)
        context.become(gather(pendingUrls + 1, fetchedUrls))
      }
    case UrlProcessed =>
      log.info("Child url processing finished")
      context.become(gather(pendingUrls, fetchedUrls + 1))
    case ParsingEnded =>
      log.info("Url parsing finished")
      if (pendingUrls == fetchedUrls) {
        log.info("Url processing finished")
        root ! UrlProcessed
        context.become(finished)
      } else {
        context.become(waitForChildren(pendingUrls, fetchedUrls))
      }
    case msg: Any =>
      unhandled(msg)
  }

  def waitForChildren(pendingUrls: Int, fetchedUrls: Int): Receive = {
    case UrlProcessed =>
      log.info("Child url processing finished")
      val newFetchedUrls = fetchedUrls + 1
      if (pendingUrls == newFetchedUrls) {
        log.info("Url processing finished")
        root ! UrlProcessed
        context.become(finished)
      } else {
        context.become(waitForChildren(pendingUrls, newFetchedUrls))
      }
    case msg: Any =>
      unhandled(msg)
  }

  def finished: Receive = {
    case msg: Any => unhandled(msg)
  }

  private def isAllowed(url: String) = {
    filters.map(url.matches).reduce(_ || _)
  }

  private def needsAnotherRound(rank: Int) = {
    rank > 0
  }
}
