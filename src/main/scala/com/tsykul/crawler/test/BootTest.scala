package com.tsykul.crawler.test

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

object BootTest extends App {
  private val system = ActorSystem("TestSystem", ConfigFactory.load("testing"))

  val producer = system.actorOf(Props[ProducerActor])

  implicit val dispatcher = system.dispatcher

  system.scheduler.scheduleOnce(FiniteDuration(10, TimeUnit.SECONDS))(producer ! DoWork(3, 2))
}
