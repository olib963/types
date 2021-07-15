package contiguous

import java.time.LocalDate

import contiguous.StandardRepresentation.DatabaseId

case class Period(start: LocalDate, end: LocalDate)
case class StandardRepresentation(periods: Map[DatabaseId, Period])

object StandardRepresentation {
  type DatabaseId = Int

  // I understand that these are usually stored as sep
  def updatePeriodEnd(state: StandardRepresentation, id: DatabaseId, newEnd: LocalDate): StandardRepresentation =
    state.periods.get(id) match {
      case None => sys.error(s"Period with Id $id does not exist")
      case Some(period) =>
        assert(period.start.isBefore(newEnd), s"$newEnd is an invalid end date! Start must be before end for $period")
        val newPeriod = period.copy(end = newEnd)
        val newState  = state.periods.updated(id, newPeriod)
        StandardRepresentation(newState) // Did this create a gap or an overlap?
    }

  def updatePeriodStart(state: StandardRepresentation, id: DatabaseId, newStart: LocalDate): StandardRepresentation =
    state.periods.get(id) match {
      case None => sys.error(s"Period with Id $id does not exist")
      case Some(period) =>
        assert(
          newStart.isBefore(period.end),
          s"$newStart is an invalid start date! Start must be before end for $period",
        )
        val newPeriod = period.copy(start = newStart)
        val newState  = state.periods.updated(id, newPeriod)
        StandardRepresentation(newState) // Did this create a gap or an overlap?
    }

  def createNewPeriod(state: StandardRepresentation, id: DatabaseId, period: Period): StandardRepresentation = {
    // Start _should_ be before end. But is it though?
    assert(period.start.isBefore(period.end), s"Period $period is invalid! Start must be before end")
    assert(!state.periods.contains(id), s"$id already exists in our state")
    state.copy(periods = state.periods.updated(id, period))
  }

  def correctlyCreateNewPeriod(
    state: StandardRepresentation,
    id: DatabaseId,
    period: Period,
  ): StandardRepresentation = {
    // Start _should_ be before end. But is it though?
    assert(period.start.isBefore(period.end), s"Period $period is invalid! Start must be before end")
    // Find period containing start date, update the end to be 1 day before start of new.
    // Find period containing end date, update start date to be 1 day after end of new.
    val (beforePeriods, startsAfterStart) = state.periods.values.partition(_.start.isBefore(period.start))
    val periodContainingStart             = ???
    val periodContainingEnd               = ???
    val newPeriods                        = state.periods
    // |------------------->||------------------->|
    //           |-------------------->|

    // becomes
    // |------->||-------------------->||-------->|
    //

    // but what about
    // |---------------------------------->||------------->|
    //           |-------------------->|

    // this becomes
    // |-------->|-------------------->|    |------------->|
    state.copy(periods = newPeriods)
  }
}

object ValidStates {
  // |----------->||----------->||------------->|
  val correct = StandardRepresentation(
    Map(
      1 -> Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31)),
      2 -> Period(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 2, 28)),
      3 -> Period(LocalDate.of(2021, 3, 1), LocalDate.of(2021, 3, 31)),
    ),
  )

  // Whoops! Wrong year
  val incorrect = StandardRepresentation(
    Map(
      1 -> Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31)),
      2 -> Period(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 2, 28)),
      3 -> Period(LocalDate.of(2021, 3, 1), LocalDate.of(2210, 3, 31)),
    ),
  )
}

object InvalidStates {

  // |----------->|              |------------->|
  //           |-------------------->|
  val overlap = StandardRepresentation(
    Map(
      1 -> Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31)),
      2 -> Period(LocalDate.of(2021, 1, 25), LocalDate.of(2021, 3, 5)),
      3 -> Period(LocalDate.of(2021, 3, 1), LocalDate.of(2021, 3, 31)),
    ),
  )
  // This can be easy to do if you are unsure if the end date is inclusive/exclusive

  // |----------->|  |------>|   |------------->|
  val gap = StandardRepresentation(
    Map(
      1 -> Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31)),
      2 -> Period(LocalDate.of(2021, 2, 5), LocalDate.of(2021, 2, 25)),
      3 -> Period(LocalDate.of(2021, 3, 1), LocalDate.of(2021, 3, 31)),
    ),
  )
  // This can also be easy to do if you are unsure if the end date is inclusive/exclusive

  // Start after end
  // |----------->||----------->||<-------------|
  val endBeforeStart = StandardRepresentation(
    Map(
      1 -> Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31)),
      2 -> Period(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 2, 28)),
      3 -> Period(LocalDate.of(2021, 3, 31), LocalDate.of(2021, 3, 1)),
    ),
  )

  case class PartSolution(d1: LocalDate, d2: LocalDate) {
    def start: LocalDate = if (d1.isBefore(d2)) d1 else d2
    def end: LocalDate   = if (d1.isAfter(d2)) d1 else d2
  }

}
