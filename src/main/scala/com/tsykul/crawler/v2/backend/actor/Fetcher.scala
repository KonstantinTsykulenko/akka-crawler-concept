package com.tsykul.crawler.v2.backend.actor

import akka.actor.{ActorRef, Actor, ActorLogging}
import akka.pattern.pipe
import com.tsykul.crawler.v2.backend.actor.state.SingleUrl
import com.tsykul.crawler.v2.backend.messages.CrawlUrlContents
import com.tsykul.crawler.worker.messages.{Url, UrlContents}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

class Fetcher(val parser: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  override def receive: Receive = {
    case url@SingleUrl(link, origin, _, depth) =>
      log.info(s"Fetching: $link, depth: $depth")
      //TODO move all normalization logic to parser
      val uri = Uri(link.replaceAll("\\s+", ""))
      val worker = sender()
      pipeline(Get(uri)).map(CrawlUrlContents(url, _, worker)).pipeTo(parser)
    case msg: Any => unhandled(msg)
  }
}
