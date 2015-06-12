package akka.hello

import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import akka.routing.RoundRobinRouter

/**
 * Created by Wilson on 2015/6/9.
 *
 * 官方计算圆周率的demo，初学者可能不好理解，先看HelloActor，再看HengHa,最后再看这个可能就好理解了
 *
 */
object Pi extends App {

  calculate(nrOfWorkers = 4, nrOfElements = 10000, nrOfMessages = 10000)

  def calculate(nrOfWorkers: Int, nrOfElements: Int, nrOfMessages: Int) {
    // Create an Akka system
    val system = ActorSystem("PiSystem")

    // create the result listener, which will print the result and shutdown the system
    val listener = system.actorOf(Props[Listener], name = "listener")

    // create the master
    val master = system.actorOf(Props(new Master(nrOfWorkers, nrOfMessages, nrOfElements, listener)), name = "master")

    // start the calculation
    master ! Calculate

  }

}

/**
 * 用来做具体计算的actor，这个就相当于工人，具体干活的
 **/
class Worker extends Actor {

  def calculatePiFor(start: Int, nrOfElements: Int): Double = {
    var acc = 0.0
    for (i <- start until (start + nrOfElements))
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  def receive = {
    case Work(start, nrOfElements) =>
      sender ! Result(calculatePiFor(start, nrOfElements)) // perform the work
  }
}

/**
 * 这个是主actor，用来任务调度的。就是领导，指挥工人干活的
 **/
class Master(nrOfWorkers: Int, nrOfMessages: Int, nrOfElements: Int, listener: ActorRef) extends Actor {

  var pi: Double = _
  var nrOfResults: Int = _
  val start: Long = System.currentTimeMillis

  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "workerRouter")

  def receive = {

    case Calculate =>
      for (i <- 0 until nrOfMessages) workerRouter ! Work(i * nrOfElements, nrOfElements)

    case Result(value) =>
      pi += value
      nrOfResults += 1
      if (nrOfResults == nrOfMessages) {

        // 将计算的结果发给listener，让listener去做最后的处理
        listener ! PiApproximation(pi, duration = (System.currentTimeMillis - start))

        // 告诉系统计算已经结束了，该停了，下班回家吃饭吧
        context.stop(self)

      }

  }

}

/**
 * 接收最后的计算结果
 **/
class Listener extends Actor {
  def receive = {
    // 处理接收到的数据
    case PiApproximation(pi, duration) =>
      println("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s".format(pi, duration))

      // shutdown不解释
      context.system.shutdown()
  }
}

/**
 * 一些entity,用来传递message, 就相当于java里面的Model
 **/
sealed trait PiMessage

case object Calculate extends PiMessage

case class Work(start: Int, nrOfElements: Int) extends PiMessage

case class Result(value: Double) extends PiMessage

case class PiApproximation(pi: Double, duration: Long)