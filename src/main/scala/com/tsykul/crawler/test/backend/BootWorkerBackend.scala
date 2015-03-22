package com.tsykul.crawler.test.backend

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterGroupSettings, ClusterRouterGroup, ClusterRouterConfig}
import akka.routing.ConsistentHashingGroup
import com.tsykul.crawler.test.backend.actor.WorkerMaster
import com.tsykul.crawler.test.backend.loadbalance.WorkerMapper
import com.typesafe.config.ConfigFactory

object BootWorkerBackend extends App  {
  val factory: ActorRefFactory = ActorSystem("TestSystem", ConfigFactory.load("testing-backend"))
  val dispatcher = factory.actorOf(
    ClusterRouterGroup(
      ConsistentHashingGroup(Nil).withHashMapper(WorkerMapper()),
      ClusterRouterGroupSettings(Int.MaxValue, List("/user/workerMaster"), true, Option("worker"))
    ).props())
  factory.actorOf(Props(classOf[WorkerMaster], dispatcher), "workerMaster")
}
