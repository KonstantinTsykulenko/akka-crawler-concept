package com.tsykul.crawler.test.frontend.actor

import akka.actor.ActorRef
import com.tsykul.crawler.test.backend.messages.{Work => BackendWork}
import com.tsykul.crawler.test.frontend.api.Work
import com.tsykul.crawler.test.frontend.protocol.WorkerProtocol._
import spray.routing._

class WorkerService(val tracker: ActorRef) extends HttpServiceActor {
  val crawlRoute =
    path("crawl") {
      post {
        entity(as[Work]) { case Work(depth, width) =>
          tracker ! BackendWork(depth, width)
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