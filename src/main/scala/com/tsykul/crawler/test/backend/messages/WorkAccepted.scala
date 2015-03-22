package com.tsykul.crawler.test.backend.messages

import akka.actor.ActorRef

case class WorkAccepted(work: Work, worker: ActorRef)
