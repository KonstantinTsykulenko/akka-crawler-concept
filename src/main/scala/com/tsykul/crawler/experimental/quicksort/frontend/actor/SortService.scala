package com.tsykul.crawler.experimental.quicksort.frontend.actor

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.tsykul.crawler.core.backend.messages.{Work => BackendWork}
import com.tsykul.crawler.experimental.quicksort.backend.actor.state.LeftSortWork
import com.tsykul.crawler.experimental.quicksort.backend.messages.SortWorkStatus
import com.tsykul.crawler.experimental.quicksort.frontend.api.{SortWorkResultObj, SortWorkResult, SortWork}
import spray.routing._
import com.tsykul.crawler.experimental.quicksort.frontend.protocol.SortWorkerProtocol._

class SortService(val tracker: ActorRef) extends HttpServiceActor {

  import context.dispatcher

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
          implicit val timeout = Timeout(5, TimeUnit.SECONDS)
          val future = tracker ? SortWorkStatus(workUid)
          onSuccess(future) {
            case result: SortWorkResult => complete(SortWorkResultObj(result))
          }
        }
      }

  def receive = runRoute(crawlRoute)
}