package akka.hello

import akka.actor._

/**
 * Created by Wilson on 2015/6/11.
 *
 * 哼哈二将,哼一次，哈一次，哼一次....
 *
 * 其实就是两个actor互发消息的demo
 *
 */
object HengHa extends App {
  val system = ActorSystem("HengHaSystem")
  val ha = system.actorOf(Props[Ha], name = "ha")
  val heng = system.actorOf(Props(new Heng(ha)), name = "heng")

  heng ! "start"
}

class Heng(ha: ActorRef) extends Actor {
  def receive = {
    case "start" =>
      ha ! "heng"

    case "ha" =>
      println("哈")
      ha ! "heng"

    case _ =>
      println("heng what?")
  }
}

class Ha extends Actor {
  def receive = {

    case "heng" =>
      println("哼")
      sender ! "ha"

    case _ =>
      println("ha what?")

  }
}