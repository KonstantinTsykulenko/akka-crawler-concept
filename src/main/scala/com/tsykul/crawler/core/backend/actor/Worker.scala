package com.tsykul.crawler.core.backend.actor

import akka.actor._
import com.tsykul.crawler.core.backend.actor.state._
import com.tsykul.crawler.core.backend.messages.{NoMoreWork, Work, WorkAccepted, WorkComplete}

abstract class Worker[RESULT, STATE <: WorkResultState[RESULT], WORK]
(parent: ActorRef, dispatcher: ActorRef)(implicit val aggregator: WorkResultAggregator[RESULT, STATE])
  extends Actor with FSM[WorkerState, WorkerMetadata[STATE, WORK]] with ActorLogging {

  startWith(Init, WorkerMetadata(null, Map.empty, Map.empty, aggregator.zero))

  whenUnhandled {
    case (Event(event, data)) =>
      log.error("Unhandled event {} in state {}", event, stateName)
      stay()
  }

  when(Init) {
    case Event(work: Work[WORK], WorkerMetadata(_, workerMapping, workMapping, state)) =>
      sender ! WorkAccepted(work, self)
      log.debug("Work received {}", work)
      goto(Dispatching) using WorkerMetadata(work, workerMapping, workMapping, splitWork(work, state))
  }

  when(Dispatching) {
    //TODO more generic function composition (lift/unlift?)
    case event: Event =>
      workDispatching.applyOrElse(event, workerHandling)
  }

  when(Waiting)(workerHandling)

  def workerHandling: StateFunction = {
    case Event(WorkAccepted(work: Work[WORK], worker), WorkerMetadata(initialWork, workerMapping, workMapping, state)) =>
      context.watch(worker)
      stay using WorkerMetadata(initialWork, workerMapping + (worker -> work), workMapping + (work.uid -> Accepted), state)
    case Event(Terminated(actor), WorkerMetadata(initialWork, workerMapping, workMapping, state)) =>
      //TODO refactor this ugly shit
      val work = workerMapping.get(actor)
      if (work.isDefined) {
        if (workMapping.get(work.get.uid).get != Completed) {
          work.map(w => {
            log.warning("Worker {} failed to process work {}", actor, w)
            dispatcher ! w
            stay using WorkerMetadata(initialWork, workerMapping - actor, workMapping + (w.uid -> Failed), state)
          }).get
        } else {
          log.debug("Leaf terminated gracefully after work completion")
          stay()
        }
      }
      else {
        stay()
      }
    case Event(WorkComplete(uid, result: RESULT), WorkerMetadata(initialWork, workerMapping, workMapping, state: STATE)) =>
      log.debug("Leaf worker: work {} complete, result {}", uid, result)
      val newWorkMapping = workMapping + (uid -> Completed)
      log.debug("Work status: {}", newWorkMapping)
      if (newWorkMapping.values.forall(_ == Completed) && stateName == Waiting) {
        val finalResult = aggregator.aggregate(result, Option(state)).transferableResult
        log.info("Work {} complete, transferring result: {}", initialWork.uid, finalResult)
        parent ! WorkComplete(initialWork.uid, finalResult)
        self ! PoisonPill
      }
      val currentWorkState = aggregator.aggregate(result, Option(state))
      log.debug("Current work {} state: {}", initialWork.uid, currentWorkState.transferableResult)
      stay using WorkerMetadata(initialWork, workerMapping, newWorkMapping, currentWorkState)
  }

  def workDispatching: StateFunction = {
    case Event(NoMoreWork, _) =>
      goto(Waiting)
    case Event(work: Work[WORK], WorkerMetadata(initialWork, workerMapping, workMapping, state)) =>
      log.debug("Work received {}", work)
      dispatcher ! work
      stay using WorkerMetadata(initialWork, workerMapping, workMapping + (work.uid -> Pending), state)
  }


  def splitWork(work: Work[WORK], state: STATE): STATE
}
