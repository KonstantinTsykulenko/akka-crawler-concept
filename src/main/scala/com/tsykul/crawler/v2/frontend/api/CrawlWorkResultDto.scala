package com.tsykul.crawler.v2.frontend.api

case class CrawlWorkResultDto(result: Option[List[String]], status: String)

object CrawlWorkResultObj {
  def apply(result: CrawlWorkResult) = {
    CrawlWorkResultDto(result.result, result.status.getClass.getSimpleName)
  }
}
