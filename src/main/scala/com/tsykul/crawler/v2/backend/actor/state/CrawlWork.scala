package com.tsykul.crawler.v2.backend.actor.state

sealed trait CrawlWork

case class SingleUrl(url: String, origin: Option[String], filters: List[String], depth: Int) extends CrawlWork
case class UrlBatch(urls: List[String], origin: Option[String], filters: List[String], depth: Int) extends CrawlWork