package com.tsykul.crawler

import akka.actor.{ActorSystem, Props}
import akka.routing.FromConfig
import com.tsykul.crawler.worker.actor.{CrawlWorker, FetcherActor, ParserActor}
import com.typesafe.config.ConfigFactory

object BootBackend extends App {
  implicit val system = ActorSystem("CrawlerSystem", ConfigFactory.load("backend"))

  val crawlWorker = system.actorOf(Props[CrawlWorker], "crawlWorker")
  val parsers = system.actorOf(Props[ParserActor].withRouter(FromConfig), "parsers")
  val fetchers = system.actorOf(Props(classOf[FetcherActor], parsers).withRouter(FromConfig), "fetchers")
}
