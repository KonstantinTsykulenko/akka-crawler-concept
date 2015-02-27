package com.tsykul.crawler.worker.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import spray.http.HttpResponse
import scala.collection.JavaConversions._

trait HtmlParser {
  def parseHtml(response: HttpResponse): List[String] = {
    val elements: List[Element] = Jsoup.parse(response.entity.asString).select("a[href]").toList
    elements.map(_.attr("href"))
  }
}
