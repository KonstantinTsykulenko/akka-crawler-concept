package com.tsykul.crawler.v2.frontend.actor

import akka.actor.{Actor, ActorLogging, ActorRefFactory}
import com.tsykul.crawler.core.backend.actor.ConsistentHashingWorkDispatcher
import com.tsykul.crawler.core.backend.messages.{Work, WorkAccepted, WorkComplete}
import com.tsykul.crawler.v2.backend.actor.state.{CrawlWorkResult => BackendWorkResult}
import com.tsykul.crawler.v2.backend.messages.CrawlWorkStatus
import com.tsykul.crawler.v2.frontend.api._

class CrawlWorkTracker extends Actor with ActorLogging with ConsistentHashingWorkDispatcher {
  override def receive: Receive = {
    case work: Work[Any] =>
      context.become(track(Map(work.uid -> CrawlWorkResult(None, Pending)).
        withDefaultValue(CrawlWorkResult(None, Unknown))))
  }

  def track(statuses: Map[String, CrawlWorkResult]): Receive = {
    case work: Work[Any] =>
      log.info("Inbound work {}", work)
      dispatcher ! work
      context.become(track(statuses + (work.uid -> CrawlWorkResult(None, Pending))))
    case WorkComplete(uid, BackendWorkResult(result)) =>
      context.become(track(statuses + (uid -> CrawlWorkResult(Some(result.asInstanceOf[List[String]]), Completed))))
      log.info("Work {} complete, result {}", uid, result)
    case WorkAccepted(work, worker) =>
      context.become(track(statuses + (work.uid -> CrawlWorkResult(None, Running))))
      log.info("Work {} accepted by {}", work, worker)
    case CrawlWorkStatus(uid) =>
      sender ! statuses(uid)
  }

  override def workers: List[String] = List("/user/workerMaster")

  override def factory: ActorRefFactory = context
}
