package com.tsykul.crawler.experimental.quicksort.frontend.actor

import akka.actor.{Actor, ActorLogging, ActorRefFactory}
import com.tsykul.crawler.core.backend.actor.ConsistentHashingWorkDispatcher
import com.tsykul.crawler.core.backend.messages.{Work, WorkAccepted, WorkComplete}
import com.tsykul.crawler.experimental.quicksort.backend.actor.state.LeftSortWorkResult
import com.tsykul.crawler.experimental.quicksort.backend.messages.SortWorkStatus
import com.tsykul.crawler.experimental.quicksort.frontend.api._

class SortWorkTracker extends Actor with ActorLogging with ConsistentHashingWorkDispatcher {
  override def receive: Receive = {
    case work: Work[Any] =>
      context.become(track(Map(work.uid -> SortWorkResult(None, Pending)).
        withDefaultValue(SortWorkResult(None, Unknown))))
  }

  def track(statuses: Map[String, SortWorkResult]): Receive = {
    case work: Work[Any] =>
      log.info("Inbound work {}", work)
      dispatcher ! work
      context.become(track(statuses + (work.uid -> SortWorkResult(None, Pending))))
    case WorkComplete(uid, LeftSortWorkResult(result)) =>
      context.become(track(statuses + (uid -> SortWorkResult(Some(result.asInstanceOf[List[Int]]), Completed))))
      log.info("Work {} complete, result {}", uid, result)
    case WorkAccepted(work, worker) =>
      context.become(track(statuses + (work.uid -> SortWorkResult(None, Running))))
      log.info("Work {} accepted by {}", work, worker)
    case SortWorkStatus(uid) =>
      sender ! statuses(uid)
  }

  override def workers: List[String] = List("/user/workerMaster")

  override def factory: ActorRefFactory = context
}
