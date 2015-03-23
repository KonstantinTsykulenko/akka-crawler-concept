package com.tsykul.crawler.v2.backend.actor.state

import com.tsykul.crawler.core.backend.actor.state.WorkResultAggregator

class CrawlWorkResultAggregator extends WorkResultAggregator[CrawlWorkResult, CrawlWorkResult] {
  override def aggregate(workResult: CrawlWorkResult, workResultAggregated: Option[CrawlWorkResult]): CrawlWorkResult = {
    val current = workResultAggregated.getOrElse(zero)
    CrawlWorkResult(workResult.fetchedUrls ::: current.fetchedUrls)
  }

  override def zero: CrawlWorkResult = CrawlWorkResult(Nil)
}
