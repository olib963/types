package lists

object StandardList {

  def describe(ints: List[Int]): ListDescription =
    ListDescription(min(ints), max(ints), sum(ints))

  def sum(ints: List[Int]): Int = ints match {
    case Nil          => 0
    case head :: tail => tail.foldLeft(head)(_ + _)
  }

  def min(ints: List[Int]): Int = ints match {
    case Nil          => sys.error("Empty lists are not allowed!")
    case head :: tail => tail.foldLeft(head)(_ min _)
  }

  def max(ints: List[Int]): Int = ints match {
    case Nil          => sys.error("Empty lists are not allowed!")
    case head :: tail => tail.foldLeft(head)(_ max _)
  }

}

object ValidStates {
  val correct = List(1, 2, 3)

  // Accidentally typed in the wrong number of -3
  val incorrect = List(1, 2, -3)
}

object InvalidStates {
  // It's possible to represent empty lists, so they have to be "handled" everywhere.
  // Above that's done by throwing exceptions
  val empty = List.empty[Int]
}

/*A common approach to solving this is to make each function total by handling the invalid states and returning
 * some "error value" for these cases. In this case we are using Option[A] to represent this.
 *
 * N.B. Total functions are functions that are defined for all of their possible input values. Partial functions
 * are only defined on a subset of input values.
 */
object StandardListWithOption {

  // sum is still the same since it was already a total function
  def sum(ints: List[Int]): Int = ints match {
    case Nil          => 0
    case head :: tail => tail.foldLeft(head)(_ + _)
  }

  // Our min and max functions can now be implemented, but they pass the problem up the call chain
  def minOption(ints: List[Int]): Option[Int] = ints match {
    case Nil          => None
    case head :: tail => Some(tail.foldLeft(head)(_ min _))
  }

  def maxOption(ints: List[Int]): Option[Int] = ints match {
    case Nil          => None
    case head :: tail => Some(tail.foldLeft(head)(_ max _))
  }

  def describeOption(ints: List[Int]): Option[ListDescription] =
    minOption(ints) match {
      case None => None
      case Some(min) =>
        maxOption(ints) match {
          // At this point we know minOption() already checked the empty list case
          case None      => sys.error("This can never happen! We've already checked for an empty list")
          case Some(max) => Some(ListDescription(min, max, sum(ints)))
        }
    }

}

object OptionClient {
  def useAPI(): Unit = {
    val description = StandardListWithOption.describeOption(List(1, 2, 3))
    // Since we are passing the buck returning Option up the chain, we have to handle it here,
    // even though we _know_ the list is not empty
    description match {
      case Some(value) => println(value)
      case None        => sys.error("I know my list is not empty!")
    }
  }
}
