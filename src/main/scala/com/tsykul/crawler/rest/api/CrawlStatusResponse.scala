package com.tsykul.crawler.rest.api

case class CrawlStatusResponse(fetchedUrls: List[String], rejectedUrls: List[String], status: String)
