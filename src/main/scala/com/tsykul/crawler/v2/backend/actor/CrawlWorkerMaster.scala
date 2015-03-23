package com.tsykul.crawler.v2.backend.actor

import akka.actor.{Props, ActorRef}
import com.tsykul.crawler.core.backend.actor.WorkerMaster
import com.tsykul.crawler.v2.backend.actor.state.{CrawlWork, CrawlWorkResultAggregator}

class CrawlWorkerMaster(dispatcher: ActorRef, aggregator: CrawlWorkResultAggregator, fetcher: ActorRef)
  extends WorkerMaster[CrawlWork, CrawlerWorker](dispatcher, classOf[CrawlerWorker], aggregator){
  override def createWorker: ActorRef = context.actorOf(Props(classOf[CrawlerWorker], sender(), dispatcher, aggregator, fetcher))
}
