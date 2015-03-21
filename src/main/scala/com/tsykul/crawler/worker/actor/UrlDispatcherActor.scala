package com.tsykul.crawler.worker.actor

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import com.tsykul.crawler.worker.messages.{RejectedUrl, CrawlMetadata, SeedUrl, Url}

class UrlDispatcherActor(val dispatcher: ActorRef) extends Actor with ActorLogging {

  override def preStart(): Unit = {
//    TODO switch to cache with max size limit or invalidate by uid somehow
    context.become(dispatch(Map.empty.withDefaultValue(Set.empty)))
  }

  override def receive: Receive = {
    //not used
    case msg: Any =>
      log.warning(s"Unhandled $msg")
      unhandled(msg)
  }

  def dispatch(inProcessing: Map[String,Set[Url]]): Receive = {
    case SeedUrl(url@Url(_, _, _, CrawlMetadata(crawlUid, _)), root, filters) =>
      log.debug(s"Dispatching $url")
      if (!inProcessing(crawlUid).contains(url)) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters, root, url, dispatcher))
      } else {
        log.info(s"Filtered out a duplicate url $url")
        root ! RejectedUrl(url)
      }
      context.become(dispatch(inProcessing.updated(crawlUid, inProcessing(crawlUid) + url)))
    case msg: Any =>
      log.warning(s"Unhandled $msg")
      unhandled(msg)
  }
}
