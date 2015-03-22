package com.tsykul.crawler.experimental.quicksort.backend.actor.state

sealed trait SortWork

case class LeftSortWork(data: List[Int]) extends SortWork
case class RightSortWork(data: List[Int]) extends SortWork