
sealed abstract class LogMessage(desc: String) {
  def content: String
  override def toString = s"\t[$desc]: $content"
}

case class Info(content: String) extends LogMessage("INFO")
case class Warning(content: String) extends LogMessage("WARNING")
case class Error(content: String) extends LogMessage("ERROR")


case class ToStore(message: LogMessage)
case class ToHold(message: LogMessage)
case object ReturnHolding


sealed trait ActorType
case object InfoStore extends ActorType
case object WarnErrorStore extends ActorType

case class RetrieveNode(node: ActorType)
case class LostNode(node: ActorType)

case class StartSending(message: LogMessage)

