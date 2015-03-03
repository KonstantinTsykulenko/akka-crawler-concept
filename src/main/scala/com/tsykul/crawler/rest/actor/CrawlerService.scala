package com.tsykul.crawler.rest.actor

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.tsykul.crawler.rest.protocol.CrawlerProtocol._
import com.tsykul.crawler.worker.actor.CrawlRootActor
import com.tsykul.crawler.worker.messages.{CrawlConfig, CrawlStatus, CrawlStatusResponse}
import spray.routing._

class CrawlerService extends HttpServiceActor {

  import context.dispatcher

  val crawlRoute =
    path("crawl") {
      post {
        entity(as[CrawlConfig]) { config =>
          val crawlUid = UUID.randomUUID.toString
          val crawlRoot = context.actorOf(Props(classOf[CrawlRootActor], crawlUid), s"crawlRoot-$crawlUid")
          crawlRoot ! config
          complete(crawlUid)
        }
      }
    } ~
    path("crawl" / Rest) { crawlUid =>
      get {
        implicit val timeout = Timeout(5, TimeUnit.SECONDS)
        val result = context.actorSelection(s"/user/crawler-service/crawlRoot-$crawlUid").resolveOne().flatMap(_ ? CrawlStatus(crawlUid))
        onSuccess(result) {
          case resp: CrawlStatusResponse => complete(resp)
        }
      }
    }

  def receive = runRoute(crawlRoute)
}