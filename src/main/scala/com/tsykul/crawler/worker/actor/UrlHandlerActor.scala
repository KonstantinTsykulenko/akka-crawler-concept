package com.tsykul.crawler.worker.actor

import akka.actor._
import com.tsykul.crawler.worker.domain._
import com.tsykul.crawler.worker.messages.{UrlProcessed, ParsingEnded, Url, UrlContents}

class UrlHandlerActor(val filters: List[String], val root: ActorRef, url: Url)
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
      stay
  }

  when(Fetching) {
    case Event(urlContents: UrlContents, _) =>
      //TODO check if this creates any significant overhead over passing from fetcher to parser directly
      parser ! urlContents
      goto(Parsing)
  }

  when(Parsing) {
    case Event(url@Url(link, rank, _), UrlHandlerData(parsed, fetched)) =>
      if (needsAnotherRound(rank) && isAllowed(link)) {
        context.actorOf(Props(classOf[UrlHandlerActor], filters, self, url))
        stay using (UrlHandlerData(parsed + 1, fetched))
      }
      else {
        stay
      }
    case Event(ended: ParsingEnded, UrlHandlerData(parsed, fetched)) =>
      //exit if terminal url
      log.info(s"Parsing ended, parsed $parsed, fetched $fetched")
      if (url.rank == 1) {
        log.info(s"Finishing processing of terminal url ${url.url}")
        root ! UrlProcessed(url)
        self ! PoisonPill
      }
      goto(Waiting)
    case Event(processed: UrlProcessed, UrlHandlerData(parsed, fetched)) =>
      stay using (UrlHandlerData(parsed, fetched + 1))
  }

  when(Waiting) {
    case Event(processed: UrlProcessed, UrlHandlerData(parsed, fetched)) =>
      val newFetched = fetched + 1
      log.info(s"Child url processing ended, parsed $parsed, fetched $newFetched")
      if (newFetched == parsed) {
        root ! UrlProcessed(url)
        self ! PoisonPill
      }
      stay using (UrlHandlerData(parsed, newFetched))
  }

  private def isAllowed(url: String) = {
    filters.map(url.matches).reduce(_ || _)
  }

  private def needsAnotherRound(rank: Int) = {
    rank > 0
  }
}
