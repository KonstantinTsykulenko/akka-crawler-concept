package com.tsykul.crawler.test

import akka.actor.{ActorLogging, ActorRefFactory, Actor}

class WorkerActor extends Actor with WorkerRouter with ActorLogging {
  override def receive: Receive = {
    case DoWork(depth, width) =>
      if (depth > 0) {
        log.info("Doing work, depth {}, width {}", depth, width)
        for (i <- 1 to width)
          router ! DoWork(depth - 1, width)
      } else {
        log.info("Work ended, depth {}, width {}", depth, width)
      }
  }

  override def factory: ActorRefFactory = context
}
