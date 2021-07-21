package contiguous

import java.time.LocalDate

// Reducing data redundancy here means that we cannot represent gaps or overlaps.
case class ContiguousPeriods(startDates: Set[LocalDate])
object ContiguousPeriods {

  def createNewPeriod(contiguousPeriods: ContiguousPeriods, date: LocalDate): ContiguousPeriods =
    contiguousPeriods.copy(startDates = contiguousPeriods.startDates + date)

  def updatePeriod(contiguousPeriods: ContiguousPeriods, oldStart: LocalDate, newStart: LocalDate): ContiguousPeriods =
    if (!contiguousPeriods.startDates.contains(oldStart)) {
      sys.error(s"No period exists starting on $oldStart")
    } else {
      contiguousPeriods.copy(startDates = (contiguousPeriods.startDates - oldStart) + newStart)
    }

}

object OnlyValidStates {
  // Just representing the start dates
  // |           |           |
  val correct = ContiguousPeriods(
    Set(
      LocalDate.of(2021, 1, 1),
      LocalDate.of(2021, 3, 1),
      LocalDate.of(2021, 2, 1),
    ),
  )

  val incorrect = ContiguousPeriods(
    Set(
      LocalDate.of(2021, 1, 1),
      LocalDate.of(2210, 3, 1), // Whoops! Wrong year
      LocalDate.of(2021, 2, 1),
    ),
  )
}

object InjectiveStateFunctions {

  case class ClosedProjection(periods: List[Period])

  def closedProjectionInclusive(contiguousPeriods: ContiguousPeriods): ClosedProjection = {
    val periods = zipWithNext(sorted(contiguousPeriods)).map { case (start, nextStart) =>
      Period(start = start, end = nextStart.minusDays(1))
    }
    ClosedProjection(periods)
  }

  case class OpenPeriod(start: LocalDate)
  case class OpenProjection(closedPeriods: List[Period], current: Option[OpenPeriod])

  def openProjectionExclusive(contiguousPeriods: ContiguousPeriods): OpenProjection = {
    val sortedDates = sorted(contiguousPeriods)
    val periods = zipWithNext(sortedDates).map { case (start, nextStart) =>
      Period(start = start, end = nextStart)
    }
    OpenProjection(closedPeriods = periods, current = sortedDates.lastOption.map(OpenPeriod))
  }

  private def sorted(contiguousPeriods: ContiguousPeriods) =
    contiguousPeriods.startDates.toList.sorted

  private def zipWithNext(sorted: List[LocalDate]) =
    sorted.zip(sorted.drop(1))
}
