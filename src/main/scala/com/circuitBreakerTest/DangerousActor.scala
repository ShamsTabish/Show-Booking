import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.{CircuitBreaker, pipe}

import scala.concurrent.Future
import scala.concurrent.duration._

class DangerousActor extends Actor {
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
    case (value, no: Int) ⇒
      Breaker.breaker.withCircuitBreaker(Future(dangerousCall(no))) pipeTo sender()
    case "block for me" ⇒
      sender() ! Breaker.breaker.withSyncCircuitBreaker(dangerousCall(-1))
  }

  def dangerousCall(no: Int): Unit = {
    val time = Calendar.getInstance()
    val count = ctr.incrementAndGet()
    println(s"$no  Retrying at :  ${time.get(Calendar.MINUTE)}  ${time.get(Calendar.SECOND)}  ===> $count")
    Thread.sleep(3000)
    throw new Exception("Failure")
  }

}

class CircuitBreakerEntryPoint(dangerousActor: ActorRef) extends Actor {
  private val value = "is my middle name"


  def JustDoIt() = {
    Range(1, 10000).map {
      t =>
        Thread.sleep(100)
        dangerousActor ! (value, t)
    }

  }

  override def receive = {
    case "DoIt" => JustDoIt()
    case n => println("\n\n\t\t\t==> Completed Actor" + n)
  }
}

object Main extends App {
  val system: ActorSystem = ActorSystem("CktBrkr")
  val actor = Props[DangerousActor]
  private val testActor: ActorRef = system.actorOf(actor, "TestActor")
  val main = Props(new CircuitBreakerEntryPoint(testActor))
  private val mainActor: ActorRef = system.actorOf(main, "MainActor")
  private val unit: Unit = mainActor ! "DoIt"


}