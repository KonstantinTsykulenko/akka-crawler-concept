package com.tsykul.crawler.core.backend.messages

import java.util.UUID

case class Work[+T](payload: T, uid: String = UUID.randomUUID().toString)