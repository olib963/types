package lists

// Now an empty list _cannot_ happen.
case class NonEmptyList[A](head: A, tail: List[A]) {
  def map[B](f: A => B): NonEmptyList[B] = NonEmptyList(f(head), tail.map(f))
}

object NonEmptyList {
  def describe(ints: NonEmptyList[Int]): ListDescription = ListDescription(min(ints), max(ints), sum(ints))

  def sum(ints: NonEmptyList[Int]): Int =
    ints.tail.foldLeft(ints.head)(_ + _)

  def min(ints: NonEmptyList[Int]): Int =
    ints.tail.foldLeft(ints.head)(_ min _)

  def max(ints: NonEmptyList[Int]): Int =
    ints.tail.foldLeft(ints.head)(_ max _)

  // Right at the edge we can do this check once and only once.
  def fromList[A](list: List[A]): Option[NonEmptyList[A]] = list match {
    case Nil          => None
    case head :: tail => Some(NonEmptyList(head, tail))
  }

  def describeList(ints: List[Int]): Option[ListDescription] = fromList(ints).map(describe)
}

object OnlyValidStates {
  val correct = NonEmptyList(1, List(2, 3))

  // Accidentally typed in the wrong nuber of -3
  val incorrect = NonEmptyList(1, List(2, -3))
}
