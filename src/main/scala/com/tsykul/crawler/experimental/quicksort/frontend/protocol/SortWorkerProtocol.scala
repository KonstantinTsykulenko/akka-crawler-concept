package com.tsykul.crawler.experimental.quicksort.frontend.protocol

import com.tsykul.crawler.experimental.quicksort.frontend.api.{SortWork, SortWorkResultDto}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object SortWorkerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
   implicit val workFormat = jsonFormat1(SortWork)
   implicit val workResultFormat = jsonFormat2(SortWorkResultDto)
 }
