package com.tsykul.crawler

import akka.actor.{ActorRef, ActorSystem, Props}
import com.tsykul.crawler.actor.UrlHandlerActor
import com.tsykul.crawler.messages.Url

import scala.concurrent.duration._

object Runner {
  def main (args: Array[String]) {
    val system = ActorSystem.create("Crawler")
    val fetcher: ActorRef = system.actorOf(Props(classOf[UrlHandlerActor], 2))
    fetcher ! Url("http://www.smartling.com")
    system.awaitTermination(1 minutes)
  }
}
