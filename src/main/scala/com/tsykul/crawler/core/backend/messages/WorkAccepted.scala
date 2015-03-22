package com.tsykul.crawler.core.backend.messages

import akka.actor.ActorRef

case class WorkAccepted[T](work: Work[T], worker: ActorRef)
