package com.tsykul.crawler.core.backend.actor.state

trait WorkResultState[T] {
  def transferableResult: T
}