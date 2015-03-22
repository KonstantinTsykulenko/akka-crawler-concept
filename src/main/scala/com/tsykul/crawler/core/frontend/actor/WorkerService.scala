package com.tsykul.crawler.core.frontend.actor

import akka.actor.ActorRef
import com.tsykul.crawler.core.backend.messages.{Work => BackendWork}
import com.tsykul.crawler.core.frontend.api.Work
import com.tsykul.crawler.core.frontend.protocol.WorkerProtocol._
import spray.routing._

class WorkerService(val tracker: ActorRef) extends HttpServiceActor {
  val crawlRoute =
    path("crawl") {
      post {
        entity(as[Work]) { case Work(depth, width) =>
//          tracker ! BackendWork(depth, width)
          complete("KK")
        }
      }
    } ~
      path("crawl" / Rest) { workUid =>
        get {
          complete("KK")
        }
      }

  def receive = runRoute(crawlRoute)
}