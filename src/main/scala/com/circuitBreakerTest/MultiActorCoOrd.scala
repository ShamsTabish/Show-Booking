package com.circuitBreakerTest

import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.{CircuitBreaker, pipe}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout

import scala.util.{Failure, Success, Try}

class EndPointHittingActor extends Actor {
  val ctr: AtomicInteger = new AtomicInteger(0)

  import context.dispatcher

  private val value = "is my middle name"

  object Breaker {

    val breaker =
      new CircuitBreaker(
        context.system.scheduler,
        maxFailures = 1,
        callTimeout = 1.seconds,
        resetTimeout = 1.seconds)
        .onOpen(notifyMeOnOpen())
        .onHalfOpen(notifyOnHalfOpen())
  }

  def notifyOnHalfOpen() = {
    println("\n\n\t\tEntered Half Open State")
  }

  def notifyMeOnOpen(): Unit =
    println("\n\n\t\t\t\tEntered Open, and will not close for half a minute")


  def receive = {
    case (value, no: Int, id: Int) ⇒
      val response = Breaker.breaker.withCircuitBreaker(Future(dangerousCall(no, id)))
      sender() ! response

    case "block for me" ⇒
      sender() ! Breaker.breaker.withSyncCircuitBreaker(dangerousCall(-1, -1))
  }

  def dangerousCall(no: Int, id: Int): Int = {
    val time = Calendar.getInstance()
    val count = ctr.incrementAndGet()
    println(s"$no  Retrying at :  ${time.get(Calendar.MINUTE)}  ${time.get(Calendar.SECOND)}  ===> $count for actor $id")
    //    Thread.sleep(3000)
    if (id > 25 && id < 70)
      throw new Exception("Failure")
    id
  }

}

class CircuitBreakerEntryPoint(testActor: ActorRef) extends Actor {
  private val value = "is my middle name"
  implicit val timeout: Timeout = 5.seconds


  def JustDoIt() = {
    Range(1, 10000).map {
      t =>
        Thread.sleep(100)
        val future = ask(testActor, (value, t, t)).mapTo[Future[Int]]
        future.flatten.map {
          m => println(s"###Response:$m")
        }
    }

  }

  val actorID = (Math.random() * 100).asInstanceOf[Int]

  override def receive = {
    case "DoIt" => JustDoIt()
    case (n) => println(s"\n\n\t\t\t==> Completed Actor $n for ID: $actorID ")
  }
}

object Commn extends App {
  val system: ActorSystem = ActorSystem("CktBrkr")
  val actor = Props[EndPointHittingActor]
  private val testActor: ActorRef = system.actorOf(actor, "TestActor")
  val main = Props(new CircuitBreakerEntryPoint(testActor))
  private val mainActor: ActorRef = system.actorOf(main, "MainActor")
  mainActor ! "DoIt"


}