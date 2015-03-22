package com.tsykul.crawler.core.backend.actor

import akka.actor.{Actor, ActorRef, Props}
import com.tsykul.crawler.core.backend.actor.state.WorkResultAggregator
import com.tsykul.crawler.core.backend.messages.Work

class WorkerMaster[WORK, WORKER <: Worker[_, _, WORK]]
(dispatcher: ActorRef, workerClass: Class[WORKER], aggregator: WorkResultAggregator[_, _]) extends Actor {
  override def receive: Receive = {
    case work: Work[WORK] => context.actorOf(Props(workerClass, sender, dispatcher, aggregator)) forward work
  }
}
