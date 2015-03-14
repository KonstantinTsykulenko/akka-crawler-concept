package com.tsykul.crawler.worker.messages

import akka.actor.ActorRef

case class SeedUrl(url: Url, root: ActorRef, filters: List[String])