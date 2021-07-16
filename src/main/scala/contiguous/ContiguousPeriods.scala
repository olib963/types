package contiguous

import java.time.LocalDate

case class ContiguousPeriods(dates: Set[LocalDate])
object ContiguousPeriods {

  def createNewPeriod(contiguousPeriods: ContiguousPeriods, date: LocalDate): ContiguousPeriods =
    contiguousPeriods.copy(dates = contiguousPeriods.dates + date)

  def updatePeriod(contiguousPeriods: ContiguousPeriods, oldDate: LocalDate, newDate: LocalDate): ContiguousPeriods =
    if (!contiguousPeriods.dates.contains(oldDate)) {
      sys.error(s"No period exists starting on $oldDate")
    } else {
      contiguousPeriods.copy(dates = (contiguousPeriods.dates - oldDate) + newDate)
    }

}

case class ClosedProjection(periods: List[Period])

case class OpenPeriod(start: LocalDate)
case class OpenProjection(closedPeriods: List[Period], current: Option[OpenPeriod])

object OnlyValidStates {
  // |           |           |           |
  val correct = ContiguousPeriods(
    Set(
      LocalDate.of(2021, 1, 1),
      LocalDate.of(2021, 3, 1),
      LocalDate.of(2021, 2, 1), // February is in here last, doesn't matter!
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

object InjectiveStateFunction {

  def closedProjectionInclusive(contiguousPeriods: ContiguousPeriods): ClosedProjection = {
    val sorted = contiguousPeriods.dates.toList.sorted
    val periods = sorted.zip(sorted.drop(1)).map { case (start, nextStart) =>
      Period(start = start, end = nextStart.minusDays(1))
    }
    ClosedProjection(periods)
  }

  def openProjectionExclusive(contiguousPeriods: ContiguousPeriods): OpenProjection = {
    val sorted = contiguousPeriods.dates.toList.sorted
    val periods = sorted.zip(sorted.drop(1)).map { case (start, nextStart) =>
      Period(start = start, end = nextStart)
    }
    OpenProjection(closedPeriods = periods, current = sorted.lastOption.map(OpenPeriod))
  }
}
