package com.tsykul.crawler.v2.backend.actor

import akka.actor.ActorRef
import com.tsykul.crawler.core.backend.actor.Worker
import com.tsykul.crawler.core.backend.messages.{WorkComplete, NoMoreWork, Work}
import com.tsykul.crawler.v2.backend.actor.state._

class CrawlerWorker(parent: ActorRef, dispatcher: ActorRef, aggregator: CrawlWorkResultAggregator, fetcher: ActorRef)
  extends Worker[CrawlWorkResult, CrawlWorkResult, CrawlWork](parent, dispatcher)(aggregator) {
  override def splitWork(work: Work[CrawlWork], state: CrawlWorkResult): CrawlWorkResult = {
    log.debug("Splitt ing work {}", work)
//    TODO handle the case when everything is filtered out
    work.payload match {
      case UrlBatch(urls, origin, filters, depth, uid) =>
        for (url <- urls)
          self ! Work(SingleUrl(url, origin, filters, depth, uid))
        self ! NoMoreWork
      case url@SingleUrl(link, origin, filters, depth, uid) =>
        if (depth > 0)
          fetcher ! SingleUrl(link, origin, filters, depth - 1, uid)
        else if (depth == 0) {
          val result = CrawlWorkResult(List(link))
          parent ! WorkComplete(work.uid, result)
          log.info("Work {} complete, transferring result: {}", work.uid, result)
        }
    }
    state
  }
}
