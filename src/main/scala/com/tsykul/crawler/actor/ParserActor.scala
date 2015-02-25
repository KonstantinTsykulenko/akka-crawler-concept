package com.tsykul.crawler.actor

import akka.actor.{Actor, ActorLogging}
import com.tsykul.crawler.messages.ParsedUrl
import com.tsykul.crawler.parser.HtmlParser
import spray.http.HttpResponse

class ParserActor extends Actor with ActorLogging with HtmlParser {
  override def receive: Receive = {
    case resp: HttpResponse => {
      val links = parseHtml(resp)
      log.info(s"links: ${links}")
      links.foreach(context.parent ! ParsedUrl(_))
    }
    case msg: Any => unhandled(msg)
  }
}
