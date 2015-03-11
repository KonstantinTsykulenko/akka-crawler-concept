package com.tsykul.crawler.worker.messages

import com.tsykul.crawler.worker.domain.{UrlInfo, CrawlRuntimeInfo, UrlStatus}

case class Url(urlInfo: UrlInfo, status: UrlStatus.Value, runtimeInfo: CrawlRuntimeInfo)