package com.tsykul.crawler

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.tsykul.crawler.rest.actor.CrawlerService
import com.tsykul.crawler.worker.actor.{FetcherActor, ParserActor}
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  implicit val system = ActorSystem("crawler")

  val service = system.actorOf(Props[CrawlerService], "crawler-service")
  val parsers = system.actorOf(Props[ParserActor].withRouter(FromConfig), "parsers")
  val fetchers = system.actorOf(Props(classOf[FetcherActor], parsers).withRouter(FromConfig), "fetchers")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
