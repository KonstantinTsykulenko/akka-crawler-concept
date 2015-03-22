package com.tsykul.crawler.core.backend.messages

case class WorkComplete[T](uid: String, result: T)
