package com.tsykul.crawler.v2.frontend.protocol

import com.tsykul.crawler.v2.frontend.api.{CrawlWork, CrawlWorkResultDto}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object CrawlWorkerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
   implicit val workFormat = jsonFormat3(CrawlWork)
   implicit val workResultFormat = jsonFormat2(CrawlWorkResultDto)
 }
