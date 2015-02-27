package com.tsykul.crawler.worker.messages

case class CrawlConfig(seeds: List[String], urlFilters: List[String], depth: Int)
