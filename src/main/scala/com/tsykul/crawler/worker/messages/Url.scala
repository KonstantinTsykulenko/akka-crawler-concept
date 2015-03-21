package com.tsykul.crawler.worker.messages

//TODO change origin to string/uri - pulling too much data otherwise
case class Url(url: String, rank: Int, origin: Option[Url] = None, metadata: CrawlMetadata)
