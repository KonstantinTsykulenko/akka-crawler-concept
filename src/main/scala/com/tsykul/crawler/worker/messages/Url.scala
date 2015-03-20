package com.tsykul.crawler.worker.messages

case class Url(url: String, rank: Int, origin: Option[Url] = None, metadata: CrawlMetadata)
