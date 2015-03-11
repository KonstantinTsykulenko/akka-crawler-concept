package com.tsykul.crawler.worker.messages

import akka.actor.ActorRef

case class GetCrawlStatus(uid: String, requester: ActorRef)