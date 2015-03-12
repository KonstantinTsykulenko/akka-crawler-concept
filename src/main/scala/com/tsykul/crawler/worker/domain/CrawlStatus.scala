package com.tsykul.crawler.worker.domain

object CrawlStatus extends Enumeration {
  type Status = Value
  val Running, Completed, Failed = Value
}
