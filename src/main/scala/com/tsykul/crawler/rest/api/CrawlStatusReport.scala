package com.tsykul.crawler.rest.api

import com.tsykul.crawler.worker.domain.CrawlStatus

case class CrawlStatusReport(fetchedUrls: List[String], status: String)
