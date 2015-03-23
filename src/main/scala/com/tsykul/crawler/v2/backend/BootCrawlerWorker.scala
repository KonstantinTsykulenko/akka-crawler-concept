package com.tsykul.crawler.v2.backend

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterGroup, ClusterRouterGroupSettings}
import akka.routing.{ConsistentHashingGroup, FromConfig}
import com.tsykul.crawler.v2.backend.actor.loadbalance.CrawlWorkMapper
import com.tsykul.crawler.v2.backend.actor.state.CrawlWorkResultAggregator
import com.tsykul.crawler.v2.backend.actor.{CrawlWorkerMaster, Fetcher, Parser}
import com.typesafe.config.ConfigFactory

object BootCrawlerWorker extends App {
  val factory: ActorRefFactory = ActorSystem("CrawlerSystem", ConfigFactory.load("backend"))
  val workers = List("/user/workerMaster")
  val parsers = factory.actorOf(Props[Parser].withRouter(FromConfig), "parsers")
  val fetchers = factory.actorOf(Props(classOf[Fetcher], parsers).withRouter(FromConfig), "fetchers")
  val dispatcher = factory.actorOf(
    ClusterRouterGroup(
      ConsistentHashingGroup(Nil).withHashMapper(CrawlWorkMapper),
      ClusterRouterGroupSettings(Int.MaxValue, workers, true, Option("worker"))
    ).props())
  //  val dispatcher = factory.actorOf(RoundRobinGroup(workers).props())
  factory.actorOf(Props(classOf[CrawlWorkerMaster], dispatcher, new CrawlWorkResultAggregator, fetchers), "workerMaster")
}
