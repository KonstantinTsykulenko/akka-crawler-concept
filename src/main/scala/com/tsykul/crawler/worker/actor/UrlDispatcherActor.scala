package com.tsykul.crawler.worker.actor

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import com.tsykul.crawler.worker.messages.{SeedUrl, Url}

class UrlDispatcherActor(val dispatcher: ActorRef) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    context.become(dispatch(Set.empty))
  }

  override def receive: Receive = {
    //not used
    case msg: Any =>
      log.warning(s"Unhandled $msg")
      unhandled(msg)
  }

  def dispatch(inProcessing: Set[Url]): Receive = {
    case SeedUrl(url, root, filters) =>
      log.debug(s"Dispatching $url")
//      TODO
//      if (!inProcessing.contains(url)) {
      if (true) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters, root, url, dispatcher))
      } else {
        log.info(s"Filtered out a duplicate url $url")
      }
      context.become(dispatch(inProcessing + url))
    case msg: Any =>
      log.warning(s"Unhandled $msg")
      unhandled(msg)
  }
}
