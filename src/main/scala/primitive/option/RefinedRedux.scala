package primitive.option

// This models only the three valid states of our application
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
}

object InjectiveStateFunction {

  def injectivelyBack(redux: RefinedRedux): StandardRedux = redux match {
    case Loading         => StandardRedux(loading = true, None, None)
    case Failed(error)   => StandardRedux(loading = false, Some(error), None)
    case Success(result) => StandardRedux(loading = false, None, Some(result))
  }

}
