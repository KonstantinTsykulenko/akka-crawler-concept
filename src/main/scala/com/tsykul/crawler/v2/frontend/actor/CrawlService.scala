package com.tsykul.crawler.v2.frontend.actor

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.tsykul.crawler.core.backend.messages.{Work => BackendWork}
import com.tsykul.crawler.v2.backend.actor.state.UrlBatch
import com.tsykul.crawler.v2.backend.messages.CrawlWorkStatus
import com.tsykul.crawler.v2.frontend.api.{CrawlWork, CrawlWorkResult, CrawlWorkResultObj}
import spray.routing._

import com.tsykul.crawler.v2.frontend.protocol.CrawlWorkerProtocol._

class CrawlService(val tracker: ActorRef) extends HttpServiceActor {

  import context.dispatcher

  val crawlRoute =
    path("crawl") {
      post {
        entity(as[CrawlWork]) { case CrawlWork(seeds, filters, depth) =>
          val uid = UUID.randomUUID().toString
          val work = BackendWork(UrlBatch(seeds, None, filters, depth, uid), uid)
          tracker ! work
          complete(work.uid)
        }
      }
    } ~
      path("crawl" / Rest) { workUid =>
        get {
          implicit val timeout = Timeout(5, TimeUnit.SECONDS)
          val future = tracker ? CrawlWorkStatus(workUid)
          onSuccess(future) {
            case result: CrawlWorkResult => complete(CrawlWorkResultObj(result))
          }
        }
      }

  def receive = runRoute(crawlRoute)
}