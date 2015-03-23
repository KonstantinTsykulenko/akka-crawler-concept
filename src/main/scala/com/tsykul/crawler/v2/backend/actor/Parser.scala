package com.tsykul.crawler.v2.backend.actor

import akka.actor.{Actor, ActorLogging}
import com.tsykul.crawler.core.backend.messages.{Work, NoMoreWork}
import com.tsykul.crawler.v2.backend.actor.state.SingleUrl
import com.tsykul.crawler.v2.backend.messages.CrawlUrlContents
import com.tsykul.crawler.v2.backend.parser.HtmlParser

class Parser extends Actor with ActorLogging with HtmlParser {

  override def receive: Receive = {
    case CrawlUrlContents(SingleUrl(url, _,  filters, depth, uid), resp, worker) =>
      log.debug(s"Parsing http response from $url")
      val links = parseHtml(resp)
      log.debug(s"links: $links")
      links.filter(link => filters.forall(link.matches)).foreach(link => worker ! Work(SingleUrl(link, Some(url), filters, depth, uid)))
      worker ! NoMoreWork
    case msg: Any => unhandled(msg)
  }
}
