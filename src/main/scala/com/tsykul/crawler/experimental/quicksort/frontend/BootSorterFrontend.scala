package com.tsykul.crawler.experimental.quicksort.frontend

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.tsykul.crawler.experimental.quicksort.frontend.actor.{SortService, SortWorkTracker}
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object BootSorterFrontend extends App {
  implicit val system = ActorSystem("TestSystem", ConfigFactory.load("testing-frontend"))

  val tracker = system.actorOf(Props[SortWorkTracker])
  val service = system.actorOf(Props(classOf[SortService], tracker).withRouter(RoundRobinPool(4)))

  implicit val timeout = Timeout(5 seconds)

  IO(Http) ? Http.Bind(service, interface = "127.0.0.1", port = 8081)
}
