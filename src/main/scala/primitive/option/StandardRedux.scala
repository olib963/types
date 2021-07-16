package primitive.option

// Our state is composed of options. (If you consider Boolean = Option[Unit], where Some(()) = true, None = false)
// These Options however are mutually exclusive, you are not supposed to be able to have more than one Some value
case class StandardRedux(loading: Boolean, failure: Option[Throwable], success: Option[String])
object StandardRedux {
  // Explicit matching shows 5 of the 8 possible states are invalid. 62.25% of states we could put the system into have
  // undefined behaviour!
  def show(standardRedux: StandardRedux): String = standardRedux match {
    case StandardRedux(true, None, None)                 => "loading"
    case StandardRedux(true, Some(error), None)          => invalidState
    case StandardRedux(true, None, Some(result))         => invalidState
    case StandardRedux(true, Some(error), Some(result))  => invalidState
    case StandardRedux(false, None, None)                => invalidState
    case StandardRedux(false, Some(error), None)         => error.getMessage
    case StandardRedux(false, None, Some(result))        => result
    case StandardRedux(false, Some(error), Some(result)) => invalidState
  }

  private def invalidState = sys.error("This should never happen, I will not code this!")

  // The more common way to get around this is to ignore or return a default value for any invalid state.
  def standardShow(standardRedux: StandardRedux): String = standardRedux match {
    case StandardRedux(true, None, None)          => "loading"
    case StandardRedux(false, Some(error), None)  => error.getMessage
    case StandardRedux(false, None, Some(result)) => result
    case _                                        => "I would never be such a fool as to put the system in any of these 5 invalid states"
  }
}
