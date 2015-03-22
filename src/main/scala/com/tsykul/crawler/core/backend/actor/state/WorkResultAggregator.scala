package com.tsykul.crawler.core.backend.actor.state

//TODO Try to generalize using fold/monoids etc
//TODO proper co/contravariance of arguments
trait WorkResultAggregator[WR, WRS <: WorkResultState[WR]] {
  def aggregate(workResult: WR, workResultAggregated: Option[WRS]): WRS

  def zero: WRS
}
