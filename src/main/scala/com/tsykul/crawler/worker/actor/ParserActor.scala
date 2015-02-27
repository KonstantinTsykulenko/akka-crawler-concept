package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging}
import com.tsykul.crawler.worker.parser.HtmlParser
import com.tsykul.crawler.worker.messages.{FetchedUrl, ParsedUrl}

class ParserActor extends Actor with ActorLogging with HtmlParser {
  override def receive: Receive = {
    case FetchedUrl(resp, origin) =>
      val links = parseHtml(resp)
      log.debug(s"links: $links")
      links.foreach(context.parent ! ParsedUrl(_, origin))
    case msg: Any => unhandled(msg)
  }
}
