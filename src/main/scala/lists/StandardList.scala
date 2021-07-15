package lists

trait Application {
  def describe(ints: List[Int]): ListDescription
}

object StandardList extends Application {

  def describe(ints: List[Int]): ListDescription = ListDescription(min(ints), max(ints), sum(ints))

  // This is a pattern match for symmetry
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

  def minOption(ints: List[Int]): Option[Int] = ints match {
    case Nil          => None
    case head :: tail => Some(tail.foldLeft(head)(_ min _))
  }

  def maxOption(ints: List[Int]): Option[Int] = ints match {
    case Nil          => None
    case head :: tail => Some(tail.foldLeft(head)(_ max _))
  }

  def boundsOption(ints: List[Int]): Option[ListDescription] =
    minOption(ints) match {
      case None => None
      case Some(min) =>
        maxOption(ints) match {
          case None      => sys.error("This can never happen! We've already checked for an empty list")
          case Some(max) => Some(ListDescription(min, max, sum(ints)))
        }
    }

}

object InvalidStates {
  val empty = List.empty[Int]
}

object ValidStates {
  val correct = List(1, 2, 3)

  // Accidentally typed in the wrong nuber of -3
  val incorrect = List(1, 2, -3)
}
