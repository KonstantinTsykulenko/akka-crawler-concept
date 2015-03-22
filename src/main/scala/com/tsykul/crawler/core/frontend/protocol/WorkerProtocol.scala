package com.tsykul.crawler.core.frontend.protocol

import com.tsykul.crawler.core.frontend.api.Work
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object WorkerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val workFormat = jsonFormat2(Work)
}
