package akka.hello.remote.benchmark

import akka.actor.Actor
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import akka.hello.remote.entity._

/**
 * 接收者, 其实这个就相当于工作的actor
 **/
class Receiver extends Actor {

  def receive = {
    case d: Directive =>
      sender() ! d

    case Shutdown =>
      context.system.shutdown()

    case _ =>

  }
}

object Receiver extends App {
  val system = ActorSystem("Sys", ConfigFactory.load("remotelookup"))
  system.actorOf(Props[Receiver], "rcv")
}