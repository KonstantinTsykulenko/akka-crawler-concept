package com.tsykul.crawler.v2.backend.messages

import akka.actor.ActorRef
import com.tsykul.crawler.v2.backend.actor.state.SingleUrl
import spray.http.HttpResponse

case class CrawlUrlContents(url: SingleUrl, contents: HttpResponse, worker: ActorRef)
