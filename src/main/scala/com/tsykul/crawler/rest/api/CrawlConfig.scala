package com.tsykul.crawler.rest.api

case class CrawlConfig(seeds: List[String], urlFilters: List[String], depth: Int)
