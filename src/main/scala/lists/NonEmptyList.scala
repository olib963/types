package lists

case class NonEmptyList[A](head: A, tail: List[A]) {
  def map[B](f: A => B): NonEmptyList[B] = NonEmptyList(f(head), tail.map(f))
}

object NonEmptyList {
  def describe(ints: NonEmptyList[Int]): ListDescription =
    ListDescription(min(ints), max(ints), sum(ints))

  def sum(ints: NonEmptyList[Int]): Int =
    ints.tail.foldLeft(ints.head)(_ + _)

  def min(ints: NonEmptyList[Int]): Int =
    ints.tail.foldLeft(ints.head)(_ min _)

  def max(ints: NonEmptyList[Int]): Int =
    ints.tail.foldLeft(ints.head)(_ max _)

}

object OnlyValidStates {
  val correct = NonEmptyList(1, List(2, 3))

  // Accidentally typed in the wrong number of -3
  val incorrect = NonEmptyList(1, List(2, -3))
}

object InjectiveStateFunction {
  def toOldState(newState: NonEmptyList[Int]): List[Int] =
    newState.head :: newState.tail
}

object OldToNew {
  // This strips out invalid states from the old representation, but the validation check only happens once, right here.
  def fromList(list: List[Int]): Option[NonEmptyList[Int]] = list match {
    case head :: tail => Some(NonEmptyList(head, tail))
    case Nil          => None
  }
}
