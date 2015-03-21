package com.tsykul.crawler.test

import akka.routing.ConsistentHashingRouter.ConsistentHashMapper

class WorkerMapper extends ConsistentHashMapper {
  override def hashKey(message: Any): Any = message.hashCode()
}
