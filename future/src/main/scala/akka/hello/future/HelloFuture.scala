package akka.hello.future

import java.util.concurrent.{TimeUnit, Executors}
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext}

/**
 * Created by Wilson on 2015/6/11.
 */
object HelloFuture extends App {
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val system = ActorSystem("HelloSystem")
  val hello = system.actorOf(Props[Hello], name = "hello")

  val future = hello ? "#########################"

  // 方式一加超时时间，不加的话死等，傻傻的等
  // val future = (hello ? "#########################")(timeout)

  // 在执行的时候请注掉一个（方式一或方式二）再执行

  // 方式一 异步
  // onSuccess和onFailure都是onComplete的特例onComplete包括前两者
  future.mapTo[String].onSuccess {
    case result: String =>
      println(result)
  }

  // 方式二 同步
  val result = Await.result(future, timeout.duration).asInstanceOf[String]
  println(result)

  println("----------------------")
}

class Hello extends Actor {
  def receive = {
    case msg: String =>
      println(msg)
      1 to 100 foreach (println(_))
      sender ! "************************"
    case _ =>
      println("unexpected message.")
  }
}