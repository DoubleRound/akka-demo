package akka.hello.remote.benchmark

import akka.actor.ActorSystem
import akka.hello.remote.entity.Continue
import com.typesafe.config.ConfigFactory

/**
 * Created by Wilson on 2015/6/16.
 */
object SimpleSender extends App {


  /**
   * 要在Akka项目中使用远程调用，最少要在 application.conf 文件中加入以下内容
   *
   * * akka {
   * *    actor {
   * *      provider = "akka.remote.RemoteActorRefProvider"
   * *    }
   * *    remote {
   * *    enabled-transports = ["akka.remote.netty.tcp"]
   * *      netty {
   * *        hostname = "127.0.0.1"
   * *        port = 2552
   * *      }
   * *    }
   * * }
   *
   **/
  val system = ActorSystem("Sys", ConfigFactory.load("calculator"))

  val actor = system.actorSelection("akka.tcp://Sys@127.0.0.1:2553/user/rcv")

  actor ! Continue(1, 1, 1, 1)

}
