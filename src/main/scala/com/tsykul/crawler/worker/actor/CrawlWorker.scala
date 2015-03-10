package com.tsykul.crawler.worker.actor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.tsykul.crawler.worker.messages.{CrawlDefinition, GetCrawlStatus}

class CrawlWorker extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case config@CrawlDefinition(_, _, _, crawlUid) =>
      val crawlRoot = context.actorOf(Props(classOf[CrawlRootActor]), s"crawlRoot-$crawlUid")
      crawlRoot ! config
    case status@GetCrawlStatus(crawlUid, requester) =>
      log.info(s"Got a status request for crawlUid: $crawlUid")
      implicit val timeout = Timeout(5, TimeUnit.SECONDS)
      val result = context.actorSelection(s"/user/crawlWorker/crawlRoot-$crawlUid").
        resolveOne().flatMap(_ ? status).map(requester ! _)
  }
}
