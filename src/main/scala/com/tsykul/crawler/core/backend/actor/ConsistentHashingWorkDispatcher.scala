package com.tsykul.crawler.core.backend.actor

import akka.actor.ActorRefFactory
import akka.cluster.routing.{ClusterRouterGroupSettings, ClusterRouterGroup}
import akka.routing.ConsistentHashingGroup
import com.tsykul.crawler.core.backend.loadbalance.WorkerMapper

trait ConsistentHashingWorkDispatcher {
  def workers: List[String]
  def factory: ActorRefFactory
  val dispatcher = factory.actorOf(
    ClusterRouterGroup(
      ConsistentHashingGroup(Nil).withHashMapper(WorkerMapper()),
      ClusterRouterGroupSettings(Int.MaxValue, workers, true, Option("worker"))
    ).props())

}