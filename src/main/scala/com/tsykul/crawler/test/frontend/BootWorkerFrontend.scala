package com.tsykul.crawler.test.frontend

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.tsykul.crawler.test.frontend.actor.{WorkTracker, WorkerService}
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object BootWorkerFrontend extends App {
  implicit val system = ActorSystem("TestSystem", ConfigFactory.load("testing-frontend"))

  val tracker = system.actorOf(Props[WorkTracker])
  val service = system.actorOf(Props(classOf[WorkerService], tracker).withRouter(RoundRobinPool(4)))

  implicit val timeout = Timeout(5 seconds)

  IO(Http) ? Http.Bind(service, interface = "127.0.0.1", port = 8081)
}
