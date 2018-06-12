import akka.actor.Actor

import scala.collection.mutable.ArrayBuffer

class DbActor extends Actor {
  val tempStorage: ArrayBuffer[LogMessage] = new ArrayBuffer[LogMessage]()

  def receive: Receive = {
    case ToStore(m) => store(m)
    case ToHold(m) => storeTemp(m)
    case ReturnHolding => returnTemp()
  }

  def store(message: LogMessage): Unit =
    println(s"Storing message $message")

  private def storeTemp(m: LogMessage) = {
    println(s"Storing temporary message $m")
    tempStorage :+ m
  }

  private def returnTemp(): Unit = {
    println("Sending back temporary hold messages")
    tempStorage.foreach(sender() ! _)
    tempStorage.clear()
  }
}
