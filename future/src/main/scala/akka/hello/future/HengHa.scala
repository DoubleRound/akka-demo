package akka.hello.future

import java.util.concurrent._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext

/**
 * Created by Wilson on 2015/6/11.
 *
 * 演示zip和fallbackTo的用法
 *
 */
object HengHa extends App {
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  implicit val timeout = Timeout(1, TimeUnit.SECONDS)

  val system = ActorSystem("HengHaSystem")

  val heng = system.actorOf(Props[Heng], name = "heng")
  val ha = system.actorOf(Props[Ha], name = "ha")

  val hengFuture = (heng ? "heng")(timeout).mapTo[String]
  val haFuture = (ha ? "ha").mapTo[String]


  // zip
  val future = hengFuture zip haFuture

  future.onSuccess {
    case (heng: String, ha: String) =>
      println(s"$heng : $ha")
      shutdown
  }

  // fallbackTo
  //  val future = hengFuture fallbackTo haFuture
  //  // val future = haFuture fallbackTo hengFuture
  //
  //  future.onSuccess {
  //    case result =>
  //      println(result)
  //      shutdown
  //  }

  def shutdown: Unit = {
    system.shutdown
    ec.shutdown
  }

}

class Heng extends Actor {
  def receive: Receive = {
    case msg: String =>
      // 1 to 100 foreach (_ => println("***"))
      // Thread.sleep(2000);
      sender ! "哼"
  }
}

class Ha extends Actor {
  def receive: Receive = {
    case msg: String =>
      // 1 to 100 foreach (_ => println("-"))
      sender ! "哈"
  }
}