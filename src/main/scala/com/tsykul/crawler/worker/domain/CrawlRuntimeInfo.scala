package com.tsykul.crawler.worker.domain

import akka.actor.ActorRef

case class CrawlRuntimeInfo(root: ActorRef, statsActor: ActorRef, uid: String)
