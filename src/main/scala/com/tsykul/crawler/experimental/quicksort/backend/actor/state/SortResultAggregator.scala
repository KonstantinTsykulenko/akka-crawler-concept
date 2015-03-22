package com.tsykul.crawler.experimental.quicksort.backend.actor.state

import com.tsykul.crawler.core.backend.actor.state.WorkResultAggregator

class SortResultAggregator extends WorkResultAggregator[SortWorkResult, SortWorkResultState] {
  override def aggregate(workResult: SortWorkResult, workResultAggregated: Option[SortWorkResultState]): SortWorkResultState = {
    val currentState = workResultAggregated.getOrElse(zero)
    workResult match {
      case LeftSortWorkResult(res) => SortWorkResultState(res, currentState.right, currentState.pivot, currentState.resultType)
      case RightSortWorkResult(res) => SortWorkResultState(currentState.left, res, currentState.pivot, currentState.resultType)
      case PivotSortWorkResult(res) => SortWorkResultState(currentState.left, currentState.right, Some(res), currentState.resultType)
      case er: EmptyResult => currentState
    }
  }

  override def zero: SortWorkResultState = SortWorkResultState(Nil, Nil, None, EmptyResult())
}
