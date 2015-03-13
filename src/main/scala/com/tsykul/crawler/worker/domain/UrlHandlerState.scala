package com.tsykul.crawler.worker.domain

sealed trait UrlHandlerState

case object Fetching extends UrlHandlerState
case object Parsing extends UrlHandlerState
case object Waiting extends UrlHandlerState
