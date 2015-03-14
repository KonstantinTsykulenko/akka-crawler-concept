package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.pipe
import com.tsykul.crawler.worker.messages.{Url, UrlContents}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

class FetcherActor extends Actor with ActorLogging {

  import context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  override def receive: Receive = {
    case url@Url(link, rank, _) =>
      log.debug(s"Fetching: $link, round: $rank")
      //TODO move all normalization logic to parser
      val uri = Uri(link.replaceAll("\\s+", ""))
      val handler = sender()
      pipeline(Get(uri)).map(UrlContents(url, _)).pipeTo(handler)
    case msg: Any => unhandled(msg)
  }
}
