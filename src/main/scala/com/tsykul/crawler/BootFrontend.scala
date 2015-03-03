package com.tsykul.crawler

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.tsykul.crawler.rest.actor.CrawlerService
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object BootFrontend extends App {
  implicit val system = ActorSystem("CrawlerSystem", ConfigFactory.load("frontend"))

  val service = system.actorOf(Props[CrawlerService], "crawlerService")

  implicit val timeout = Timeout(5 seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
