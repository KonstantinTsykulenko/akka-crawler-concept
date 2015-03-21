package com.tsykul.crawler.rest.protocol

import com.tsykul.crawler.rest.api.{CrawlConfig, CrawlStatusResponse}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object CrawlerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val crawlConfigFormat = jsonFormat3(CrawlConfig)
  implicit val crawlStatusResponseFormat = jsonFormat3(CrawlStatusResponse)
}
