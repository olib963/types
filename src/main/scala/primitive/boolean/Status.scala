package primitive.boolean

/*
 * For this example it's entirely possible that we only started with one boolean. Let's say
 * we originally had State(isActive: Boolean). We likely needed to introduce a new state which
 * we did by simply adding a new boolean, this enabled invalid states. When adding new features we can
 * always take a moment to consider how it affects the whole data model.
 */
case class Status(isActive: Boolean, isDeleted: Boolean)
object Status {
  def show(myStatus: Status): String = myStatus match {
    case Status(true, false)  => "Active"
    case Status(false, false) => "InActive"
    case Status(false, true)  => "Deleted"
    case Status(true, true)   => sys.error("It's not possible to be active _and_ deleted")
  }
}
