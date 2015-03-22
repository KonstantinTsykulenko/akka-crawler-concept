package com.tsykul.crawler.core.backend.actor.state

import akka.actor.ActorRef
import com.tsykul.crawler.core.backend.messages.Work

case class WorkerMetadata[STATE, WORK](initialWork: Work[WORK], workerMapping: Map[ActorRef, Work[WORK]], workMapping: Map[String, WorkState], state: STATE)
