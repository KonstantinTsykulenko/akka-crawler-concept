package com.tsykul.crawler.experimental.quicksort.frontend.api

case class SortWorkResultDto(result: Option[List[Int]], status: String)

object SortWorkResultObj {
  def apply(result: SortWorkResult) = {
    SortWorkResultDto(result.result, result.status.getClass.getSimpleName)
  }
}
