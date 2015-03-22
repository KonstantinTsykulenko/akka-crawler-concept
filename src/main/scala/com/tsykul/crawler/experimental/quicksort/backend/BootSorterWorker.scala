package com.tsykul.crawler.experimental.quicksort.backend

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterGroup, ClusterRouterGroupSettings}
import akka.routing.ConsistentHashingGroup
import com.tsykul.crawler.core.backend.actor.WorkerMaster
import com.tsykul.crawler.core.backend.loadbalance.WorkerMapper
import com.tsykul.crawler.experimental.quicksort.backend.actor.SortWorker
import com.tsykul.crawler.experimental.quicksort.backend.actor.state.{SortResultAggregator, SortWork}
import com.typesafe.config.ConfigFactory

object BootSorterWorker extends App {
  val factory: ActorRefFactory = ActorSystem("TestSystem", ConfigFactory.load("testing-backend"))
  val dispatcher = factory.actorOf(
    ClusterRouterGroup(
      ConsistentHashingGroup(Nil).withHashMapper(WorkerMapper()),
      ClusterRouterGroupSettings(Int.MaxValue, List("/user/workerMaster"), true, Option("worker"))
    ).props())
  factory.actorOf(Props(classOf[WorkerMaster[SortWork, SortWorker]], dispatcher, classOf[SortWorker], new SortResultAggregator), "workerMaster")
}
