package com.tsykul.crawler

import akka.actor.{ActorRef, ActorSystem, Props}
import com.tsykul.crawler.actor.CrawlRootActor
import com.tsykul.crawler.messages.{CrawlConfig, Url}

import scala.concurrent.duration._

object Runner {
  def main(args: Array[String]) {
    val system = ActorSystem.create("Crawler")
    val fetcher: ActorRef = system.actorOf(Props(classOf[CrawlRootActor]))
    fetcher ! CrawlConfig(List("http://www.smartling.com"), List("http://www.smartling.com.*"), 2)
    system.awaitTermination(1 minutes)
  }
}
