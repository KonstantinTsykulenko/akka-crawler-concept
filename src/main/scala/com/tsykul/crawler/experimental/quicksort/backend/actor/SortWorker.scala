package com.tsykul.crawler.experimental.quicksort.backend.actor

import akka.actor.ActorRef
import com.tsykul.crawler.experimental.quicksort.backend.actor.state._
import com.tsykul.crawler.core.backend.actor.Worker
import com.tsykul.crawler.core.backend.messages.{NoMoreWork, Work, WorkComplete}

class SortWorker(parent: ActorRef, dispatcher: ActorRef)(aggregator: SortResultAggregator)
  extends Worker[SortWorkResult, SortWorkResultState, SortWork](parent, dispatcher)(aggregator) {

  def handleWork(list: List[Int], work: Work[SortWork], state: SortWorkResultState) = {
    list match {
      case Nil =>
        val result = state.transferableResult
        val uid = work.uid
        log.info("Work {} complete, transferring result: {}", uid, result)
        parent ! WorkComplete(uid, result)
      case head :: tail =>
        self ! WorkComplete(null, PivotSortWorkResult(head))
        val (lesser, greater) = tail.partition(_ < head)
        self ! Work(LeftSortWork(lesser))
        self ! Work(RightSortWork(greater))
        self ! NoMoreWork
    }
  }

  override def splitWork(work: Work[SortWork], state: SortWorkResultState): SortWorkResultState = {
    work.payload match {
      case LeftSortWork(list) =>
        val resultState = SortWorkResultState(Nil, Nil, None, LeftSortWorkResult(Nil))
        handleWork(list, work, resultState)
        resultState
      case RightSortWork(list) =>
        val resultState = SortWorkResultState(Nil, Nil, None, RightSortWorkResult(Nil))
        handleWork(list, work, resultState)
        resultState
    }
  }
}
