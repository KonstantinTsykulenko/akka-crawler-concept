package com.tsykul.crawler.test.backend.actor

import akka.actor.ActorRefFactory
import akka.cluster.routing.{ClusterRouterGroupSettings, ClusterRouterGroup}
import akka.routing.ConsistentHashingGroup
import com.tsykul.crawler.test.backend.loadbalance.WorkerMapper

trait WorkDispatcher {
  def workers: List[String]
  def factory: ActorRefFactory
  val dispatcher = factory.actorOf(
    ClusterRouterGroup(
      ConsistentHashingGroup(Nil).withHashMapper(WorkerMapper()),
      ClusterRouterGroupSettings(Int.MaxValue, workers, true, Option("worker"))
    ).props())

}
