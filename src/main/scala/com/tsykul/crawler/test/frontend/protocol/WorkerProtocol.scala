package com.tsykul.crawler.test.frontend.protocol

import com.tsykul.crawler.test.frontend.api.Work
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object WorkerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val workFormat = jsonFormat2(Work)
}
