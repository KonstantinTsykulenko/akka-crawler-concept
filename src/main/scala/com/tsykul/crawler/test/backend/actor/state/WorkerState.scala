package com.tsykul.crawler.test.backend.actor.state

sealed trait WorkerState

case object Init extends WorkerState
case object Dispatching extends WorkerState
case object Waiting extends WorkerState