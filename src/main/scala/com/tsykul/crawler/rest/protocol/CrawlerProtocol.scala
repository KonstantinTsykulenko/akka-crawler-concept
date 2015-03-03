package com.tsykul.crawler.rest.protocol

import com.tsykul.crawler.worker.messages.{CrawlStatus, CrawlStatusResponse, CrawlConfig}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object CrawlerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val crawlConfigFormat = jsonFormat3(CrawlConfig)
  implicit val crawlStatusFormat = jsonFormat1(CrawlStatus)
  implicit val crawlStatusResponseFormat = jsonFormat2(CrawlStatusResponse)
}
