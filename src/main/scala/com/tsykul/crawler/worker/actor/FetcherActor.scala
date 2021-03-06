package com.tsykul.crawler.worker.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.pipe
import com.tsykul.crawler.worker.domain.UrlInfo
import com.tsykul.crawler.worker.domain.UrlStatus._
import com.tsykul.crawler.worker.messages.{UrlContents, Url}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

class FetcherActor(val parser: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  override def receive: Receive = {
    case Url(info@UrlInfo(link, rank, _), Approved, runtimeStatus) =>
      log.info(s"Fetching: $link, round: $rank")
      //TODO move all normalization logic to parser
      val uri = Uri(link.replaceAll("\\s+", ""))
      pipeline(Get(uri)).map(UrlContents(Url(info, Fetched, runtimeStatus), _)).pipeTo(parser)
    case msg: Any => unhandled(msg)
  }
}
