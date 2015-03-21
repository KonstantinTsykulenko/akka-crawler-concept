package com.tsykul.crawler.worker.messages

import akka.actor.ActorRef

case class CrawlMetadata(uid: String, statsCollector: ActorRef)
