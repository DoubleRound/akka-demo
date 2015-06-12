package akka.hello

import akka.actor.{Props, ActorSystem, Actor}

/**
 * Created by Wilson on 2015/6/9.
 *
 * 最基本的HelloWorld
 *
 */
object Test extends App {

  val system = ActorSystem("HelloSystem")

  val helloActor = system.actorOf(Props[Hello], name = "helloActor")

  //上面是创建Actor，下面是给actor发消息。准确点说是创建容器(ActorSystem),把Actor(Hello)放入容器里，而不是创建actor。

  helloActor ! "hello"

  helloActor ! 123

  helloActor ! 112.3

  helloActor ! Message("lalalala", 7)

  // 最后关闭容器
  system.shutdown

}

class Hello extends Actor {
  def receive = {
    case str: String =>
      println(s"str=$str")

    case int: Int =>
      println(s"number=$int")

    case Message(str, int) =>
      println(s"str=$str int=$int")

    case _ =>
      println("what are you 弄啥咧?")
  }

}

// 如果有scala基础的就不用看了，没有的，就认为是java里的Model吧
case class Message(str: String, int: Int)