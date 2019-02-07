package com.circuitBreakerTest

import java.util.Calendar
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.{CircuitBreaker, pipe}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

case class A_SYNCH_MSG()

case class SYNCH_MSG()

case class START()

class MultiTaskingActor extends Actor {
  val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3))

  val ctr: AtomicInteger = new AtomicInteger(0)

  import context.dispatcher


  val breaker =
    new CircuitBreaker(
      context.system.scheduler,
      maxFailures = 3,
      callTimeout = 1.seconds,
      resetTimeout = 2.seconds)
      .onOpen(notifyMeOnOpen())
      .onHalfOpen(notifyOnHalfOpen())

  def notifyOnHalfOpen() = println("\n\n\t\tEntered Half Open State")

  def notifyMeOnOpen(): Unit = println("\n\n\t\t\t\tEntered Open, and will not close for half a minute")

  def receive = {
    case (msg: A_SYNCH_MSG, no: Int) ⇒
      breaker.withCircuitBreaker(Future(dangerousCall(no))(ec)) pipeTo sender()
    case m: SYNCH_MSG ⇒
      sender() ! breaker.withSyncCircuitBreaker(dangerousCall(-1))
  }

  def dangerousCall(no: Int): Unit = {
    val time = Calendar.getInstance()
    val count = ctr.incrementAndGet()
    println(s"$no  Retrying at :  ${time.get(Calendar.MINUTE)}  ${time.get(Calendar.SECOND)}  ===> $count")
    Thread.sleep(3000)
    throw new Exception("Failure")
  }

}

class GatewayActor(testActor: ActorRef) extends Actor {

  def JustDoIt() = {
    Range(1, 10000).map {
      t =>
        Thread.sleep(100)
        testActor ! (A_SYNCH_MSG(), t)
    }
  }

  override def receive = {
    case s: START =>
      println("Command Received to Begin")
      JustDoIt()
    case n => println("\n\n\t\t\t==> Completed Actor" + n)
  }
}

object Main extends App {
  val system: ActorSystem = ActorSystem("CktBrkr")
  val actor = Props[MultiTaskingActor]
  private val testActor: ActorRef = system.actorOf(actor, "TestActor")
  val main = Props(new GatewayActor(testActor))
  private val mainActor: ActorRef = system.actorOf(main, "MainActor")
  println("Starting from Main")
  mainActor ! START()


}