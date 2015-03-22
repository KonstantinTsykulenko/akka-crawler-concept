package com.tsykul.crawler.core.frontend.actor

import akka.actor.{ActorLogging, ActorRefFactory, Actor}
import com.tsykul.crawler.core.backend.actor.ConsistentHashingWorkDispatcher
import com.tsykul.crawler.core.backend.messages.{WorkAccepted, WorkComplete, Work}

class WorkTracker extends Actor with ActorLogging with ConsistentHashingWorkDispatcher {
  override def receive: Receive = {
    case work: Work[Any] => dispatcher ! work
    case WorkComplete(uid, result) => log.info("Work {} complete, result {}", uid, result)
    case WorkAccepted(work, worker) => log.info("Work {} accepted by {}", work, worker)
  }

  override def workers: List[String] = List("/user/workerMaster")

  override def factory: ActorRefFactory = context
}
