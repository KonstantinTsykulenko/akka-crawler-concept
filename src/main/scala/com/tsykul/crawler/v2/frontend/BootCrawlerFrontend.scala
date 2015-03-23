package com.tsykul.crawler.v2.frontend

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.tsykul.crawler.v2.frontend.actor.{CrawlService, CrawlWorkTracker}
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object BootCrawlerFrontend extends App {
  implicit val system = ActorSystem("CrawlerSystem", ConfigFactory.load("frontend"))

  val tracker = system.actorOf(Props[CrawlWorkTracker])
  val service = system.actorOf(Props(classOf[CrawlService], tracker).withRouter(RoundRobinPool(4)))

  implicit val timeout = Timeout(5 seconds)

  IO(Http) ? Http.Bind(service, interface = "127.0.0.1", port = 8081)
}
