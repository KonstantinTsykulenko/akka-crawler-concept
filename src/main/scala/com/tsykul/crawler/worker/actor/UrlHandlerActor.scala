package com.tsykul.crawler.worker.actor

import akka.actor._
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import com.tsykul.crawler.worker.domain._
import com.tsykul.crawler.worker.messages._

class UrlHandlerActor(val filters: List[String], val root: ActorRef, url: Url, dispatcher: ActorRef)
  extends Actor with ActorLogging with FSM[UrlHandlerState, UrlHandlerData] {

  //TODO use some form of injection
  val fetcher = context.actorSelection("/user/fetchers")
  val parser = context.actorSelection("/user/parsers")

  startWith(Fetching, UrlHandlerData(0, 0))

  override def preStart(): Unit = {
    fetcher ! url
  }

  whenUnhandled {
    case Event(e, s) =>
      log.warning(s"Unhandled, state $stateName, message $e")
      stay()
  }

  when(Fetching) {
    case Event(urlContents: UrlContents, _) =>
      //TODO check if this creates any significant overhead over passing from fetcher to parser directly
      parser ! urlContents
      goto(Parsing)
  }

  when(Parsing) {
    case Event(url@Url(link, rank, _, _), UrlHandlerData(parsed, fetched)) =>
      if (needsAnotherRound(rank) && isAllowed(link)) {
        log.info(s"Rebalancing $url, dispatcher $dispatcher")
        dispatcher ! ConsistentHashableEnvelope(SeedUrl(url, self, filters), link)
        stay using UrlHandlerData(parsed + 1, fetched)
      } else {
        stay()
      }
    case Event(ended: ParsingEnded, UrlHandlerData(parsed, fetched)) =>
      //exit if terminal url
      log.debug(s"Parsing ended, parsed $parsed, fetched $fetched")
      if (url.rank == 1) {
        log.info(s"Finishing processing of terminal url ${url.url}")
        root ! ProcessedUrl(url)
        log.info(s"Reporting end of processing to root $root")
        self ! PoisonPill
      }
      goto(Waiting)
    case Event(processed@ProcessedUrl(fetchedUrl@Url(_, _, _, CrawlMetadata(_, statsCollector))), UrlHandlerData(parsed, fetched)) =>
      val newFetched = fetched + 1
      statsCollector ! FetchedUrl(fetchedUrl)
      log.info(s"Child url processing ended, parsed $parsed, fetched $newFetched, sender ${sender()}")
      stay using UrlHandlerData(parsed, newFetched)
    case Event(rejected@RejectedUrl(Url(_, _, _, CrawlMetadata(_, statsCollector))), UrlHandlerData(parsed, fetched)) =>
      val newFetched = fetched + 1
      statsCollector ! rejected
      log.info(s"Child url rejected, parsed $parsed, fetched $newFetched, sender ${sender()}")
      stay using UrlHandlerData(parsed, newFetched)
  }

  when(Waiting) {
    case Event(processed@ProcessedUrl(fetchedUrl@Url(_, _, _, CrawlMetadata(_, statsCollector))), UrlHandlerData(parsed, fetched)) =>
      val newFetched = fetched + 1
      statsCollector ! FetchedUrl(fetchedUrl)
      log.info(s"Child url processing ended, parsed $parsed, fetched $newFetched, sender ${sender()}")
      if (newFetched == parsed) {
        root ! ProcessedUrl(url)
        self ! PoisonPill
      }
      stay using UrlHandlerData(parsed, newFetched)
    case Event(rejected@RejectedUrl(Url(_, _, _, CrawlMetadata(_, statsCollector))), UrlHandlerData(parsed, fetched)) =>
      val newFetched = fetched + 1
      statsCollector ! rejected
      log.info(s"Child url rejected, parsed $parsed, fetched $newFetched, sender ${sender()}")
      stay using UrlHandlerData(parsed, newFetched)
  }

  private def isAllowed(url: String) = {
    filters.map(url.matches).reduce(_ || _)
  }

  private def needsAnotherRound(rank: Int) = {
    rank > 0
  }
}
