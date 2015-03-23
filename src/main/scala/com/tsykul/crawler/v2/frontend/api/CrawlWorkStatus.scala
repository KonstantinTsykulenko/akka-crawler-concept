package com.tsykul.crawler.v2.frontend.api

sealed trait CrawlWorkStatus

case object Pending extends CrawlWorkStatus
case object Running extends CrawlWorkStatus
case object Completed extends CrawlWorkStatus
case object Failed extends CrawlWorkStatus
case object Unknown extends CrawlWorkStatus
