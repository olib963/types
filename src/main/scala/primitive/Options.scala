package primitive

case class StandardRedux(loading: Boolean, failure: Option[Throwable], success: Option[String])
object StandardRedux {
  def show(standardRedux: StandardRedux): String = standardRedux match {
    case StandardRedux(true, None, None)                 => "loading"
    case StandardRedux(true, Some(error), None)          => ???
    case StandardRedux(true, None, Some(result))         => ???
    case StandardRedux(true, Some(error), Some(result))  => ???
    case StandardRedux(false, None, None)                => ???
    case StandardRedux(false, Some(error), None)         => error.getMessage
    case StandardRedux(false, None, Some(result))        => result
    case StandardRedux(false, Some(error), Some(result)) => ???
  }

  def standardShow(standardRedux: StandardRedux): String = standardRedux match {
    case StandardRedux(true, None, None)          => "loading"
    case StandardRedux(false, Some(error), None)  => error.getMessage
    case StandardRedux(false, None, Some(result)) => result
    case _                                        => "I would never be such a fool as to put the system in any of these 5 invalid states"
  }
}

sealed trait RefinedRedux
case object Loading                 extends RefinedRedux
case class Failed(error: Throwable) extends RefinedRedux
case class Success(result: String)  extends RefinedRedux

object RefinedRedux {
  def show(redux: RefinedRedux): String = redux match {
    case Loading         => "loading"
    case Failed(error)   => error.getMessage
    case Success(result) => result
  }

  def injectivelyBack(redux: RefinedRedux): StandardRedux = redux match {
    case Loading         => StandardRedux(loading = true, None, None)
    case Failed(error)   => StandardRedux(loading = false, Some(error), None)
    case Success(result) => StandardRedux(loading = false, None, Some(result))
  }

}
