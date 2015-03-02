package com.tsykul.crawler.worker.messages

import akka.actor.ActorRef

case class CrawlRuntimeInfo(root: ActorRef, statsActor: ActorRef, uid: String)
