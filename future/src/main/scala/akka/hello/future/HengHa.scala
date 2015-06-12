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
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val system = ActorSystem("HengHaSystem")

  val heng = system.actorOf(Props[Heng], name = "heng")
  val ha = system.actorOf(Props[Ha], name = "ha")

  val hengFuture = (heng ? "heng")(timeout).mapTo[String]
  val haFuture = (ha ? "ha").mapTo[String]


  // zip zip用于等待两个future都完成（两个互不耽误，同时进行，可能完成时间有先后）
  // 比如计算1到100的和，第一个计算1到50，第二个计算51到100，等到两个都完成之后将结果进行相加
  // 三个的话就是 f1 zip f2 zip f3 不过这个的解析结构是((result1, result2), result3) 同理四个的就是（((result1, result2), result3), result4）
  // 注意：这个只要有一个出现异常就算是结束了
    val future = hengFuture zip haFuture
    future.onSuccess {
      case (heng: String, ha: String) =>
        println(s"$heng : $ha")
        shutdown
    }

  // fallbackTo 用于保证性计算，
  // 比如去爬取www.baidu.com这个页面：
  // 假如第一个连接异常,或超时，第二个成功，则返回第二个的结果；
  // 假如第二个连接异常或者其他异常，则返回第一个的结果；
  // 如果两个都异常，那就真蛋疼了，查找问题吧...；
  // 如果两个都成功，则顺序取第一个的；即使第二个比第一个先完成也是取第一个(它会等到两个都完成（一个完成一个异常也算完成）才会返回结果)
  //  val future = hengFuture fallbackTo haFuture // 两个都成功返回hengFuture的
  //  // val future = haFuture fallbackTo hengFuture // 两个都成功返回haFuture的
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
      Thread.sleep(2000);
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