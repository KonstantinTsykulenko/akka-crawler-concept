package com.tsykul.crawler.v2.backend.actor

import akka.actor.{Props, ActorRef}
import com.tsykul.crawler.core.backend.actor.WorkerMaster
import com.tsykul.crawler.core.backend.messages.{WorkComplete, Work}
import com.tsykul.crawler.v2.backend.actor.state.{CrawlWorkResult, SingleUrl, CrawlWork, CrawlWorkResultAggregator}

//TODO use distributed collections and some form of eviction
class CrawlWorkerMaster(dispatcher: ActorRef, aggregator: CrawlWorkResultAggregator, fetcher: ActorRef)
  extends WorkerMaster[CrawlWork, CrawlerWorker](dispatcher, classOf[CrawlerWorker], aggregator){
  override def receive: Receive = {
    case w@Work(SingleUrl(url, _, _, _), _) =>
      createWorker forward w
      context become(filtering(Set.empty))
    case w: Work[Any] => createWorker forward w
  }

  def filtering(visited: Set[String]): Receive = {
    case w@Work(SingleUrl(url, _, _, _), uid) =>
      if (!visited.contains(url))
        createWorker forward w
      else {
        log.info("Filtering out already visited url {}", url)
        sender ! WorkComplete(uid, CrawlWorkResult(Nil))
      }
      context become(filtering(visited + url))
    case w: Work[Any] =>
      createWorker forward w
  }

  override def createWorker: ActorRef = context.actorOf(Props(classOf[CrawlerWorker], sender(), dispatcher, aggregator, fetcher))
}
