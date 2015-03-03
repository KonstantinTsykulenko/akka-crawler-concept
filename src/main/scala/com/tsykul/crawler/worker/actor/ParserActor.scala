package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging}
import com.tsykul.crawler.worker.messages.{CrawlRuntimeInfo, FetchedUrl, ParsedUrl, Url}
import com.tsykul.crawler.worker.parser.HtmlParser

class ParserActor extends Actor with ActorLogging with HtmlParser {

  override def receive: Receive = {
    case fetchedUrl@FetchedUrl(resp, origin@Url(url, _, _, CrawlRuntimeInfo(root, stats, _))) =>
      stats ! fetchedUrl
      log.info(s"Parsing http response from $url")
      val links = parseHtml(resp)
      log.debug(s"links: $links")
      links.foreach(root ! ParsedUrl(_, origin))
    case msg: Any => unhandled(msg)
  }
}
