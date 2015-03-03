package com.tsykul.crawler.worker.messages

case class CrawlDefinition(seeds: List[String], urlFilters: List[String], depth: Int, uid: String)
