package com.tsykul.crawler.test.backend.actor

import akka.actor._
import com.tsykul.crawler.test.backend.actor.state._
import com.tsykul.crawler.test.backend.messages.{NoMoreWork, Work, WorkAccepted, WorkComplete}

class Worker
(parent: ActorRef, dispatcher: ActorRef) extends Actor with FSM[WorkerState, WorkerMetadata] with ActorLogging {

  startWith(Init, WorkerMetadata(null, Map.empty, Map.empty))

  whenUnhandled {
    case (Event(event, data)) =>
      log.error("Unhandled event {} in state {}", event, stateName)
      stay()
  }

  when(Init) {
    case Event(work: Work, WorkerMetadata(_, workerMapping, workMapping)) =>
      sender ! WorkAccepted(work, self)
      splitWork(work)
      goto(Dispatching) using WorkerMetadata(work, workerMapping, workMapping)
  }

  when(Dispatching) {
    //TODO more generic function composition (lift/unlift?)
    case event: Event =>
      workDispatching.applyOrElse(event, workerHandling)
  }

  when(Waiting)(workerHandling)

  def workerHandling: StateFunction = {
    case Event(WorkAccepted(work, worker), WorkerMetadata(initialWork, workerMapping, workMapping)) =>
      context.watch(worker)
      stay using WorkerMetadata(initialWork, workerMapping + (worker -> work), workMapping + (work.uid -> Accepted))
    case Event(Terminated(actor), WorkerMetadata(initialWork, workerMapping, workMapping)) =>
      //TODO refactor this ugly shit
      val work = workerMapping.get(actor)
      if (work.isDefined) {
        if (workMapping.get(work.get.uid).get != Completed) {
          work.map(w => {
            log.warning("Worker {} failed to process work {}", actor, w)
            dispatcher ! w
            stay using WorkerMetadata(initialWork, workerMapping - actor, workMapping + (w.uid -> Failed))
          }).get
        } else {
          log.info("Leaf terminated gracefully afrer work completion")
          stay()
        }
      }
      else {
        stay()
      }
    case Event(WorkComplete(work), WorkerMetadata(initialWork, workerMapping, workMapping)) =>
      log.info("Leaf worker: work complete {}", work)
      val newWorkMapping = workMapping + (work.uid -> Completed)
      log.debug("Work status: {}", newWorkMapping)
      if (newWorkMapping.values.forall(_ == Completed)) {
        parent ! WorkComplete(initialWork)
        self ! PoisonPill
      }
      stay using WorkerMetadata(initialWork, workerMapping, newWorkMapping)
  }

  def workDispatching: StateFunction = {
    case Event(NoMoreWork, _) =>
      goto(Waiting)
    case Event(work: Work, WorkerMetadata(initialWork, workerMapping, workMapping)) =>
      dispatcher ! work
      stay using WorkerMetadata(initialWork, workerMapping, workMapping + (work.uid -> Pending))
  }


  def splitWork(work: Work) = {
    //imitate work
    Thread.sleep(5000)
    if (work.depth > 0) {
      log.info("Executing work: {}", work)
      for (i <- 1 to work.width) {
        self ! Work(work.depth - 1, work.width)
      }
      self ! NoMoreWork
    }
    else {
      parent ! WorkComplete(work)
      log.info("Work ended: {}", work)
    }
  }
}
