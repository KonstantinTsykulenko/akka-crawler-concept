package com.tsykul.crawler.test.backend.actor.state

import akka.actor.ActorRef
import com.tsykul.crawler.test.backend.messages.Work

case class WorkerMetadata(initialWork: Work, workerMapping: Map[ActorRef, Work], workMapping: Map[String, WorkState])
