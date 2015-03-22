package com.tsykul.crawler.experimental.quicksort.backend.actor.state

sealed trait SortWorkResult

case class LeftSortWorkResult(result: List[Int]) extends SortWorkResult
case class RightSortWorkResult(result: List[Int]) extends SortWorkResult
case class PivotSortWorkResult(result: Int) extends SortWorkResult
case class EmptyResult() extends SortWorkResult