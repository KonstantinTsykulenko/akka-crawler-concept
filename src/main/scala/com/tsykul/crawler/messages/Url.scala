package com.tsykul.crawler.messages

case class Url(url: String, rank: Int, origin: Option[String] = None)