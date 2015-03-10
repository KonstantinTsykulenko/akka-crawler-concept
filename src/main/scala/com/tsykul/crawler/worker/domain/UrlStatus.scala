package com.tsykul.crawler.worker.domain

object UrlStatus extends Enumeration {
  type CrawlNodeStatus = Value
  val Parsed, Approved, Fetched, Failed, Rejected = Value
}
