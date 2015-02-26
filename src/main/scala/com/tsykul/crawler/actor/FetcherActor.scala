package com.tsykul.crawler.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.pipe
import com.tsykul.crawler.messages.{FetchedUrl, Url}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

class FetcherActor(val parser: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  override def receive: Receive = {
    case url@Url(link, rank, origin) =>
      log.info(s"fetching: $link, round: $rank")
      val uri = Uri(link.replaceAll("\\s+", ""))
      pipeline(Get(uri)).map(FetchedUrl(_, url)).pipeTo(parser)
    case msg: Any => unhandled(msg)
  }
}
