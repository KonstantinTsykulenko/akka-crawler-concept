package com.tsykul.crawler

import akka.actor.{ActorSystem, Props}
import akka.routing.FromConfig
import com.tsykul.crawler.worker.actor.{UrlDispatcherActor, CrawlWorkerActor, FetcherActor, ParserActor}
import com.typesafe.config.ConfigFactory

object BootBackend extends App {
  implicit val system = ActorSystem("CrawlerSystem", ConfigFactory.load("backend"))

  val dispatcher = system.actorOf(FromConfig.props(Props(classOf[UrlDispatcherActor], null)),
    name = "urlDispatchers")
  val crawlWorker = system.actorOf(Props(classOf[CrawlWorkerActor], dispatcher), "crawlWorker")
  val urlDispatcher = system.actorOf(Props(classOf[UrlDispatcherActor], dispatcher), "urlDispatcher")
  val parsers = system.actorOf(Props[ParserActor].withRouter(FromConfig), "parsers")
  val fetchers = system.actorOf(Props(classOf[FetcherActor]).withRouter(FromConfig), "fetchers")
}
