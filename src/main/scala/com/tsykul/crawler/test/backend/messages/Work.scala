package com.tsykul.crawler.test.backend.messages

import java.util.UUID

case class Work(depth: Int, width: Int, uid: String = UUID.randomUUID().toString)