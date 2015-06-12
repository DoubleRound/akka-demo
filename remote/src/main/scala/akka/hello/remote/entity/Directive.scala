package akka.hello.remote.entity

/**
 * Created by Wilson on 2015/6/10.
 */
sealed trait Directive

case object Shutdown

case object Start extends Directive

case object Done extends Directive

case class Continue(remaining: Int, startTime: Long, burstStartTime: Long, n: Int) extends Directive