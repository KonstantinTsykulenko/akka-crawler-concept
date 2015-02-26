package com.tsykul.crawler.messages

case class CrawlConfig(seeds: List[String], urlFilters: List[String], depth: Int)
