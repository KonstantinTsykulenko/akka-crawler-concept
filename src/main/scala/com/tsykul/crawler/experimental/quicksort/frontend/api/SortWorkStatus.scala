package com.tsykul.crawler.experimental.quicksort.frontend.api

sealed trait SortWorkStatus

case object Pending extends SortWorkStatus
case object Running extends SortWorkStatus
case object Completed extends SortWorkStatus
case object Failed extends SortWorkStatus
case object Unknown extends SortWorkStatus
