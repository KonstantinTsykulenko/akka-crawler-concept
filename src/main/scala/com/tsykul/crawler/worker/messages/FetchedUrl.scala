package com.tsykul.crawler.worker.messages

import spray.http.HttpResponse

case class FetchedUrl(response: HttpResponse, origin: Url)
