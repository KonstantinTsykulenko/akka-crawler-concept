package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging}
import com.tsykul.crawler.v2.backend.parser.HtmlParser
import com.tsykul.crawler.worker.messages.{ParsingEnded, Url, UrlContents}

class ParserActor extends Actor with ActorLogging with HtmlParser {

  override def receive: Receive = {
    case fetchedUrl@UrlContents(origin@Url(url, rank, _, metadata), resp) =>
      log.debug(s"Parsing http response from $url")
      val links = parseHtml(resp)
      log.debug(s"links: $links")
      links.foreach(link => sender ! Url(link, rank - 1, Option(origin), metadata))
      sender ! ParsingEnded(origin)
    case msg: Any => unhandled(msg)
  }
}
