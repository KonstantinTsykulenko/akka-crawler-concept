package com.tsykul.crawler.v2.backend.actor.loadbalance

import akka.routing.ConsistentHashingRouter.ConsistentHashMapper
import com.tsykul.crawler.core.backend.messages.Work
import com.tsykul.crawler.v2.backend.actor.state.{UrlBatch, SingleUrl}

object CrawlWorkMapper extends ConsistentHashMapper {
  override def hashKey(message: Any): Any = {
    message match {
      case Work(SingleUrl(url, _, _, _), _) => url
      case Work(batch@UrlBatch, _) => batch.hashCode()
      case msg: Any => msg.hashCode()
    }
  }
}
