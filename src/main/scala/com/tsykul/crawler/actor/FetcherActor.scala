package com.tsykul.crawler.actor

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import com.tsykul.crawler.messages.{FetchedUrl, Url}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

import akka.pattern.pipe

class FetcherActor(val parser: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  override def receive: Receive = {
    case Url(url) => {
      log.info(s"fetching: ${url}")
      pipeline(Get(url)).pipeTo(parser).map(_ => FetchedUrl(url)).pipeTo(context.parent)
    }
    case msg: Any => unhandled(msg)
  }
}
