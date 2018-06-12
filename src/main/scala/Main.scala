import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main extends App {
  ActorSystem("DistributedDbSystem", ConfigFactory.load("local/"))
}

object RegisterInfoStore extends App {
  private val system = ActorSystem("InfoStoreSystem", ConfigFactory.load("local/infostore"))
  system.actorOf(Props[DbActor], "infostore")

  println("InfoStore started")
}


object RegisterWarnErrorStore extends App {
  private val system = ActorSystem("WarnErrorStoreSystem",
    ConfigFactory.load("local/warnerrorstore"))
  system.actorOf(Props[DbActor], "warnerrorstore")

  println("WarnErrorStore started")
}

object RegisterMediator extends App {
  private val system = ActorSystem("WarnErrorStoreSystem",
    ConfigFactory.load("local/mediator"))
  val infoStorePath = "akka.tcp://InfoStoreSystem@127.0.0.1:2552/user/infostore"

  val warnErrorStorePath = "akka.tcp://WarnErrorStoreSystem@127.0.0.1:2553/user/warnerrorstore"

  system.actorOf(Props(new Mediator()), "mediator")

  println("Mediator started")
}
