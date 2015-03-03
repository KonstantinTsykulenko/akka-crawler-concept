package com.tsykul.crawler.worker.messages

import akka.actor.ActorRef

case class CrawlStatus(uid: String, requester: ActorRef)