package com.tsykul.crawler.worker.parser

import org.jsoup.Jsoup
import spray.http.HttpResponse

import scala.collection.JavaConversions._

trait HtmlParser {
  def parseHtml(response: HttpResponse): List[String] = {
    Jsoup.parse(response.entity.asString).select("a[href]").toList.map(_.attr("href"))
  }
}
