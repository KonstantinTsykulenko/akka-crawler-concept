package com.tsykul.crawler.worker.domain

object UrlStatus extends Enumeration {
  type CrawlNodeStatus = Value
  val Pending, Approved, Fetched, Parsed, Rejected, Failed = Value
}
