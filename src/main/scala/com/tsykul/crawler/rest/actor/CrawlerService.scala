package com.tsykul.crawler.rest.actor

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.pattern.ask
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig
import akka.util.Timeout
import com.tsykul.crawler.rest.api.{CrawlConfig, CrawlStatusResponse}
import com.tsykul.crawler.rest.protocol.CrawlerProtocol._
import com.tsykul.crawler.worker.actor.CrawlWorkerActor
import com.tsykul.crawler.worker.messages.{CrawlDefinition, GetCrawlStatus}
import spray.routing._

class CrawlerService extends HttpServiceActor {

  import context.dispatcher

  val workerRouter = context.actorOf(FromConfig.props(Props(classOf[CrawlWorkerActor], null)),
    name = "crawlWorkers")

  val crawlRoute =
    path("crawl") {
      post {
        entity(as[CrawlConfig]) { case CrawlConfig(seeds, urlFilters, depth) =>
          val crawlUid = UUID.randomUUID.toString
          workerRouter ! ConsistentHashableEnvelope(CrawlDefinition(seeds, urlFilters, depth, crawlUid), crawlUid)
          complete(crawlUid)
        }
      }
    } ~
      path("crawl" / Rest) { crawlUid =>
        get {
          implicit val timeout = Timeout(5, TimeUnit.SECONDS)
          val handler = context.actorOf(Props(classOf[CrawlStatusResultHandler], workerRouter))
          //TODO Try to get ask working directly without an intermediate actor
          val result = handler ? ConsistentHashableEnvelope(GetCrawlStatus(crawlUid, self), crawlUid)
          onSuccess(result) {
            case resp: CrawlStatusResponse => complete(resp)
          }
        }
      }

  def receive = runRoute(crawlRoute)
}