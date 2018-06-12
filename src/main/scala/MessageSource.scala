import akka.actor.{Actor, ActorRef}
import concurrent.duration._

class MessageSource(mediator: ActorRef) extends Actor {

  import context.dispatcher

  def receive: Receive = {
    case StartSending(message) =>
      context.system.scheduler.schedule(0 seconds, 1 second)(mediator ! message)
  }
}
