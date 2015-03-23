package com.tsykul.crawler.v2.backend.actor.state

import com.tsykul.crawler.core.backend.actor.state.WorkResultState

case class CrawlWorkResult(fetchedUrls: List[String]) extends WorkResultState[CrawlWorkResult] {
  override def transferableResult: CrawlWorkResult = this
}