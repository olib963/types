package primitive

case class MyStatus(isActive: Boolean, isDeleted: Boolean)
object MyStatus {
  def toString(myStatus: MyStatus): String = myStatus match {
    case MyStatus(true, false)  => "Active"
    case MyStatus(false, false) => "InActive"
    case MyStatus(false, true)  => "Deleted"
    case MyStatus(true, true)   => ???
  }
}

sealed trait MyADTStatus
case object Active   extends MyADTStatus
case object InActive extends MyADTStatus
case object Deleted  extends MyADTStatus

object MyADTStatus {
  def toString(myStatus: MyADTStatus): String = myStatus match {
    case Active   => "Active"
    case InActive => "InActive"
    case Deleted  => "Deleted"
  }
}
