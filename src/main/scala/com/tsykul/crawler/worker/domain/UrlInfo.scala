package com.tsykul.crawler.worker.domain

case class UrlInfo(url: String, rank: Int, origin: Option[UrlInfo] = None)
