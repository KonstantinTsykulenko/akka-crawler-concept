package com.tsykul.crawler.core.backend.actor

import akka.actor.{ActorRef, ActorRefFactory}

trait WorkDispatcher {
  def workers: List[String]
  def factory: ActorRefFactory
  val dispatcher: ActorRef
}
