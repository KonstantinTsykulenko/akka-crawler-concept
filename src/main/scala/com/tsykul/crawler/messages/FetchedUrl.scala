package com.tsykul.crawler.messages

import spray.http.HttpResponse

case class FetchedUrl(response: HttpResponse, origin: Url)
