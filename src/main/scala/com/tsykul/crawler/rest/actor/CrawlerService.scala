package com.tsykul.crawler.rest.actor

import java.util.UUID

import akka.actor.Props
import com.tsykul.crawler.rest.protocol.CrawlerProtocol._
import com.tsykul.crawler.worker.actor.CrawlRootActor
import com.tsykul.crawler.worker.messages.CrawlConfig
import spray.routing._

class CrawlerService extends HttpServiceActor {

  val crawlRoute =
    path("crawl") {
      post {
        entity(as[CrawlConfig]) { config =>
          val crawlUid = UUID.randomUUID.toString
          val crawlRoot = context.actorOf(Props(classOf[CrawlRootActor], crawlUid), s"crawlRoot-$crawlUid")
          crawlRoot ! config
          complete("OK")
        }
      }
    }

  def receive = runRoute(crawlRoute)
}