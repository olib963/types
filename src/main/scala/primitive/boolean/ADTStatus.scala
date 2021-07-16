package primitive.boolean

sealed trait ADTStatus
case object Active   extends ADTStatus
case object InActive extends ADTStatus
case object Deleted  extends ADTStatus

object ADTStatus {

  def show(status: ADTStatus): String = status match {
    case Active   => "Active"
    case InActive => "InActive"
    case Deleted  => "Deleted"
  }

}

object InjectiveStateFunction {

  def injectivelyBack(status: ADTStatus): Status = status match {
    case Active   => Status(isActive = true, isDeleted = false)
    case InActive => Status(isActive = false, isDeleted = false)
    case Deleted  => Status(isActive = false, isDeleted = true)
  }

}

object OldToNew {
  def toADT(status: Status): Option[ADTStatus] = status match {
    case Status(true, false)  => Some(Active)
    case Status(false, false) => Some(InActive)
    case Status(false, true)  => Some(Deleted)
    case Status(true, true)   => None
  }
}
