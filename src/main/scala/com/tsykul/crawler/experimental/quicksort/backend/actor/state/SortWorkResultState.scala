package com.tsykul.crawler.experimental.quicksort.backend.actor.state

import com.tsykul.crawler.core.backend.actor.state.WorkResultState

case class SortWorkResultState(left: List[Int], right: List[Int], pivot: Option[Int], resultType: SortWorkResult) extends WorkResultState[SortWorkResult] {
  def transferableResult = {
    def concatResult = {
      if (pivot.isDefined)
        left ::: pivot.get :: right
      else
        Nil
    }
    resultType match {
      case res: LeftSortWorkResult => LeftSortWorkResult(concatResult)
      case res: RightSortWorkResult => RightSortWorkResult(concatResult)
      case res: PivotSortWorkResult => PivotSortWorkResult(pivot.get)
      case res: EmptyResult => res
    }

  }
}
