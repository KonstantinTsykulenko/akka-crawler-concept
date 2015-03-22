package com.tsykul.crawler.core.backend.actor

import akka.routing.RoundRobinGroup

trait LocalWorkDispatcher extends WorkDispatcher {
  override val dispatcher = factory.actorOf(RoundRobinGroup(workers).props())
}
