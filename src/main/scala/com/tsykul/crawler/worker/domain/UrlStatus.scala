package com.tsykul.crawler.worker.domain

object UrlStatus extends Enumeration {
  type Status = Value
  val Pending, Approved, Fetched, Parsed, Rejected, Failed = Value
}
