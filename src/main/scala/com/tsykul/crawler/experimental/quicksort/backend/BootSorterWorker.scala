package com.tsykul.crawler.experimental.quicksort.backend

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterGroup, ClusterRouterGroupSettings}
import akka.routing.{RoundRobinGroup, ConsistentHashingGroup}
import com.tsykul.crawler.core.backend.actor.WorkerMaster
import com.tsykul.crawler.core.backend.loadbalance.WorkerMapper
import com.tsykul.crawler.experimental.quicksort.backend.actor.SortWorker
import com.tsykul.crawler.experimental.quicksort.backend.actor.state.{SortResultAggregator, SortWork}
import com.typesafe.config.ConfigFactory

object BootSorterWorker extends App {
  val factory: ActorRefFactory = ActorSystem("TestSystem", ConfigFactory.load("testing-backend"))
  val workers = List("/user/workerMaster")
  val dispatcher = factory.actorOf(
    ClusterRouterGroup(
      ConsistentHashingGroup(Nil).withHashMapper(WorkerMapper()),
      ClusterRouterGroupSettings(Int.MaxValue, workers, true, Option("worker"))
    ).props())
//  val dispatcher = factory.actorOf(RoundRobinGroup(workers).props())
  factory.actorOf(Props(classOf[WorkerMaster[SortWork, SortWorker]], dispatcher, classOf[SortWorker], new SortResultAggregator), "workerMaster")
}
