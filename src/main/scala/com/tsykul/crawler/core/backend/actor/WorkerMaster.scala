package com.tsykul.crawler.core.backend.actor

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import com.tsykul.crawler.core.backend.actor.state.WorkResultAggregator
import com.tsykul.crawler.core.backend.messages.Work

class WorkerMaster[WORK, WORKER <: Worker[_, _, WORK]]
(dispatcher: ActorRef, workerClass: Class[WORKER], aggregator: WorkResultAggregator[_, _]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case work: Work[WORK] =>
      val worker = createWorker
      log.debug("Forwarding work {} to worker {}", work, worker)
      worker forward work
  }

  def createWorker: ActorRef = {
    context.actorOf(Props(workerClass, sender, dispatcher, aggregator))
  }
}
