package com.tsykul.crawler.worker.messages

case class CrawlStatusResponse(parsedUrls: List[String], fetchedUrls: List[String])
