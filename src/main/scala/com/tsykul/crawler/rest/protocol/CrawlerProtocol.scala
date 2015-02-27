package com.tsykul.crawler.rest.protocol

import com.tsykul.crawler.worker.messages.CrawlConfig
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object CrawlerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val crawlConfigFormat = jsonFormat3(CrawlConfig)
}
