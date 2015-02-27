package com.tsykul.crawler.rest.actor

import akka.actor.Props
import com.tsykul.crawler.worker.actor.CrawlRootActor
import com.tsykul.crawler.worker.messages.CrawlConfig
import spray.http.MediaTypes._
import spray.routing._
import com.tsykul.crawler.rest.protocol.CrawlerProtocol._

class CrawlerService extends HttpServiceActor {

  val crawlRoute =
    path("crawl") {
      post {
        entity(as[CrawlConfig]) { config =>
          val crawlRoot = context.actorOf(Props(classOf[CrawlRootActor]))
          crawlRoot ! config
          complete("OK")
        }
      }
    }

  val myRoute =
    path("crawl") {
      get {
        complete("OK")
      }
    }

  def receive = runRoute(crawlRoute)
}