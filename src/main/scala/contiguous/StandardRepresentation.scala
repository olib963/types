package contiguous

import java.time.LocalDate

import contiguous.StandardRepresentation.DatabaseId

case class Period(start: LocalDate, end: LocalDate)
case class StandardRepresentation(periods: Map[DatabaseId, Period])

object StandardRepresentation {
  type DatabaseId = Int

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
    state.copy(periods = state.periods.updated(id, period)) // Did this create a gap or an overlap?
  }

  /** A function that attempts to correctly insert a new period into the timeline. Usually this is
    * left to clients to do by using the above manipulating functions.
    *
    * Assuming the old state and new period we want is:
    *  |------------------->||------------------->| (old)
    *            |-------------------->| (new)
    *
    * We want to end up with:
    * |------->||-------------------->||-------->|
    */
  def correctlyCreateNewPeriod(
    state: StandardRepresentation,
    id: DatabaseId,
    period: Period,
  ): StandardRepresentation = {
    // Start _should_ be before end. But is it though?
    assert(period.start.isBefore(period.end), s"Period $period is invalid! Start must be before end")

    // Find period containing the start date, if it exist update the end to be 1 day before start of new period.
    val lastPeriodStartingBeforeNewPeriod =
      state.periods.toList
        .sortBy(_._2.start)
        .findLast(_._2.start.isBefore(period.start))

    val updatedState = lastPeriodStartingBeforeNewPeriod match {
      case Some((id, _)) => updatePeriodEnd(state, id, period.start.minusDays(1))
      case None          => state
    }

    // Find period containing the end date, if it exists update start date to be 1 day after end of new period.
    val firstPeriodEndingAfterNewPeriod =
      state.periods.toList
        .sortBy(_._2.start)
        .find(_._2.end.isAfter(period.end))

    val stateWithNewStart = firstPeriodEndingAfterNewPeriod match {
      case Some((id, _)) => updatePeriodStart(updatedState, id, period.end.plusDays(1))
      case None          => updatedState
    }

    // This function checks yet again that start is before end, even though we just checked that!
    createNewPeriod(stateWithNewStart, id, period)
    /*
     * This function is a valiant attempt to correctly integrate a new period holding the constraints
     * of no gaps or overlaps, but is as a result insanely complex.
     *
     * It also makes the assumption that the state is already correct. is the behaviour desired in this case?
     * If we already have a gap/overlap what should happen?
     *
     * There are a couple of subtle bugs in the above! Can you find them?
     */
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

}
