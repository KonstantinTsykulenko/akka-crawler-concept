package com.tsykul.crawler.core.backend.loadbalance

import akka.routing.ConsistentHashingRouter.ConsistentHashMapper

class WorkerMapper extends ConsistentHashMapper {
  override def hashKey(message: Any): Any = message.hashCode()
}

object WorkerMapper {
  def apply() = new WorkerMapper
}
