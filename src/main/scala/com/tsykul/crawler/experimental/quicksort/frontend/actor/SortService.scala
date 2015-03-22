package com.tsykul.crawler.experimental.quicksort.frontend.actor

import akka.actor.ActorRef
import com.tsykul.crawler.core.backend.messages.{Work => BackendWork}
import com.tsykul.crawler.experimental.quicksort.backend.actor.state.LeftSortWork
import com.tsykul.crawler.experimental.quicksort.frontend.api.SortWork
import spray.routing._
import com.tsykul.crawler.experimental.quicksort.frontend.protocol.SortWorkerProtocol._

class SortService(val tracker: ActorRef) extends HttpServiceActor {
  val crawlRoute =
    path("sort") {
      post {
        entity(as[SortWork]) { case SortWork(list) =>
          val work: BackendWork[LeftSortWork] = BackendWork(LeftSortWork(list))
          tracker ! work
          complete(work.uid)
        }
      }
    } ~
      path("sort" / Rest) { workUid =>
        get {
          complete("KK")
        }
      }

  def receive = runRoute(crawlRoute)
}