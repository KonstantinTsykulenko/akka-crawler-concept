package com.tsykul.crawler.test.backend.actor.state

sealed trait WorkState

case object Pending extends WorkState
case object Accepted extends WorkState
case object Completed extends WorkState
case object Failed extends WorkState
