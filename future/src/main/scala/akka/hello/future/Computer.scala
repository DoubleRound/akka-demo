package akka.hello.future

import java.util.concurrent._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext

/**
 * Created by Wilson on 2015/6/11.
 *
 * 演示如何计算1到20000自然数的和
 *
 */
object Computer extends App {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  implicit val timeout = Timeout(1, TimeUnit.SECONDS)

  val system = ActorSystem("ComputeSystem")

  val compute = system.actorOf(Props[Compute], name = "compute")

  val f1 = (compute ? Data(1, 10000)).mapTo[BigInt]

  val f2 = (compute ? Data(10001, 20000))(timeout).mapTo[BigInt]

  val sumFuture = f1 zip f2

  sumFuture.onSuccess {
    case (sum1, sum2) =>
      println(sum1 + sum2)
      shutdown
  }

  def shutdown(): Unit = {
    system.shutdown
    ec.shutdown
  }

}

case class Data(start: BigInt, end: BigInt)

class Compute extends Actor {
  override def receive = {
    case Data(start, end) =>
      sender ! (start to end).par.reduceLeft(_ + _)
  }
}
