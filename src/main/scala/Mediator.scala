import akka.actor.{Actor, ActorIdentity, ActorRef}
import akka.remote.ContainerFormats.ActorIdentity

class Mediator(infoDbPath: String, warnErrorDbPath: String) extends Actor {


  def receive: Receive = identifyingBoth(infoDbPath, warnErrorDbPath)

  def identifyingBoth(infoDbPath: String, warnErrorDbPath: String): Receive = {
    case ActorIdentity(path, Some(actor)) =>
      if (path == infoDbPath)
        context become identifyingOne(actor, warnErrorDbPath)
      else if (path == warnErrorDbPath)
        context become identifyingOne(actor, infoDbPath)

    case ActorIdentity(path, None) => println("Can't reach host: " + path)
  }

  def identifyingOne(identified: ActorRef, actorPath: String): Receive = {
    case ActorIdentity(path, Some(actor)) =>
      if (path == infoDbPath)
        activate(actor, identified)
      else if (path == warnErrorDbPath)
        activate(identified, actor)

    case ActorIdentity(path, None) => println("Can't reach host: " + path)
  }

  private def activate(infoDb: ActorRef, warnErrorDb: ActorRef) = {
    context.watchWith(infoDb, LostNode(InfoStore))
    context.watchWith(warnErrorDb, LostNode(WarnErrorStore))
    context become active(infoDb, warnErrorDb)
  }

  def active(infoDb: ActorRef, warnErrorDb: ActorRef): Receive = {
    case m: Info => infoDb ! ToStore(m)
    case m: LogMessage => warnErrorDb ! ToStore(m)

    case LostNode(node) =>
      println(s"Losing $node")
      node match {
        case InfoStore => context become infoNodeLost(warnErrorDb)
        case WarnErrorStore => context become warnErrorNodeLost(infoDb)
      }
  }

  def infoNodeLost(tempDb: ActorRef): Receive = {
    case m: Info => tempDb ! ToHold(m)
    case m: LogMessage => tempDb ! ToStore(m)

    case RetrieveNode(node) => retrieve(node)
  }

  def warnErrorNodeLost(tempDb: ActorRef): Receive = {
    case m: Info => tempDb ! ToStore(m)
    case m: LogMessage => tempDb ! ToHold(m)

    case RetrieveNode(node) => retrieve(node)
  }


  private def retrieve(node: ActorType): Unit = {
    println(s"Retrieving $node")
    context unbecome()
    node match {
      case InfoStore => warnErrorDb ! ReturnHolding
      case WarnErrorStore => infoDb ! ReturnHolding
    }
  }
}
