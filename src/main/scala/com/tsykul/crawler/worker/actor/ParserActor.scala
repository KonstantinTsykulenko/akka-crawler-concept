package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging}
import com.tsykul.crawler.worker.domain.UrlStatus.Fetched
import com.tsykul.crawler.worker.domain.{UrlStatus, CrawlRuntimeInfo, UrlInfo}
import com.tsykul.crawler.worker.messages.{ParsingEnded, Url, UrlContents}
import com.tsykul.crawler.worker.parser.HtmlParser

class ParserActor extends Actor with ActorLogging with HtmlParser {

  override def receive: Receive = {
    case UrlContents(fetchedUrl@Url(origin@UrlInfo(url, rank, _), Fetched, runtimeInfo@CrawlRuntimeInfo(root, stats, _)), resp) =>
      stats ! fetchedUrl
      log.info(s"Parsing http response from $url")
      val links = parseHtml(resp)
      log.debug(s"links: $links")
      links.foreach(link => root ! Url(UrlInfo(link, rank - 1, Option(origin)), UrlStatus.Parsed, runtimeInfo))
      root ! ParsingEnded
    case msg: Any => unhandled(msg)
  }
}
