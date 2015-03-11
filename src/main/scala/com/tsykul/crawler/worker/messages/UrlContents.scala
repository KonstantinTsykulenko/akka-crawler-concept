package com.tsykul.crawler.worker.messages

import spray.http.HttpResponse

case class UrlContents(url: Url, httpResponse: HttpResponse)
