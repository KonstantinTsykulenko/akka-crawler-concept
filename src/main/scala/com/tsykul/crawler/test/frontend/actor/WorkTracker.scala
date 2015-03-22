package com.tsykul.crawler.test.frontend.actor

import akka.actor.{ActorLogging, ActorRefFactory, Actor}
import com.tsykul.crawler.test.backend.actor.WorkDispatcher
import com.tsykul.crawler.test.backend.messages.{WorkAccepted, WorkComplete, Work}

class WorkTracker extends Actor with ActorLogging with WorkDispatcher {
  override def receive: Receive = {
    case work: Work => dispatcher ! work
    case WorkComplete(work) => log.info("Work {} complete", work)
    case WorkAccepted(work, worker) => log.info("Work {} accepted by {}", work, worker)
  }

  override def workers: List[String] = List("/user/workerMaster")

  override def factory: ActorRefFactory = context
}
