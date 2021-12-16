package contiguous

import (
	"errors"
	"fmt"
	"sort"
	"time"
)

type Period struct {
	start time.Time
	end   time.Time
}

type DatabaseId = int64
type StandardRepresentation struct {
	periods map[DatabaseId]Period
}

func updatePeriodEnd(state StandardRepresentation, id DatabaseId, newEnd time.Time) (StandardRepresentation, error) {
	period, exists := state.periods[id]
	if !exists {
		return state, errors.New(fmt.Sprintf("Period with Id %v does not exist", id))
	}
	if !period.start.Before(newEnd) {
		return state, errors.New(fmt.Sprintf("%v is an invalid end date! Start must be before end for %+v", newEnd, period))
	}

	newPeriod := Period{
		start: period.start,
		end:   newEnd,
	}
	// Did this create a gap or an overlap?
	state.periods[id] = newPeriod
	return state, nil
}

func updatePeriodStart(state StandardRepresentation, id DatabaseId, newStart time.Time) (StandardRepresentation, error) {
	period, exists := state.periods[id]
	if !exists {
		return state, errors.New(fmt.Sprintf("Period with Id %v does not exist", id))
	}

	if !newStart.Before(period.end) {
		return state, errors.New(fmt.Sprintf("%v is an invalid end date! Start must be before end for %+v", newStart, period))
	}
	newPeriod := Period{
		start: newStart,
		end:   period.end,
	}
	// Did this create a gap or an overlap?
	state.periods[id] = newPeriod
	return state, nil
}

func createNewPeriod(state StandardRepresentation, id DatabaseId, period Period) (StandardRepresentation, error) {
	// Start _should_ be before end. But is it though?
	if !period.start.Before(period.end) {
		return state, errors.New(fmt.Sprintf("Period %+v is invalid! Start must be before end", period))
	}
	if _, exists := state.periods[id]; exists {
		return state, errors.New(fmt.Sprintf("%v already exists in our state", id))
	}
	// Did this create a gap or an overlap?
	state.periods[id] = period
	return state, nil
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
func correctlyCreateNewPeriod(
	state StandardRepresentation,
	id DatabaseId,
	period Period,
) (StandardRepresentation, error) {
	if !period.start.Before(period.end) {
		return state, errors.New(fmt.Sprintf("Period %+v is invalid! Start must be before end", period))
	}

	// Find period containing the start date, if it exist update the end to be 1 day before start of new period.
	var lastPeriodStartingBeforeNewPeriod *DatabaseId
	periods := toSlice(state.periods)
	sort.Slice(periods, func(i, j int) bool {
		return periods[i].value.start.Before(periods[j].value.start)
	})
	for _, pair := range periods {
		if pair.value.start.Before(period.start) {
			lastPeriodStartingBeforeNewPeriod = &pair.key
		}
	}

	oneDay := 24 * time.Hour
	if lastPeriodStartingBeforeNewPeriod != nil {
		newEndDate := period.start.Add(-1 * oneDay) // You can add a duration, but not subtract it -_-
		state, err := updatePeriodEnd(state, *lastPeriodStartingBeforeNewPeriod, newEndDate)
		if err != nil {
			return state, err
		}
	}

	// Find period containing the end date, if it exists update start date to be 1 day after end of new period.
	reverse(periods)
	var firstPeriodEndingAfterNewPeriod *DatabaseId
	for _, pair := range periods {
		if pair.value.end.After(period.end) {
			firstPeriodEndingAfterNewPeriod = &pair.key
		}
	}
	if firstPeriodEndingAfterNewPeriod != nil {
		newStartDate := period.end.Add(oneDay)
		state, err := updatePeriodStart(state, *firstPeriodEndingAfterNewPeriod, newStartDate)
		if err != nil {
			return state, err
		}
	}

	// This function checks yet again that start is before end, even though we just checked that!
	return createNewPeriod(state, id, period)
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

func reverse(list []pair) {
	for i, j := 0, len(list)-1; i < j; i, j = i+1, j-1 {
		list[i], list[j] = list[j], list[i]
	}
}

type pair struct {
	key   DatabaseId
	value Period
}

func toSlice(periods map[DatabaseId]Period) []pair {
	results := make([]pair, 0)
	for id, period := range periods {
		results = append(results, pair{key: id, value: period})
	}
	return results
}

var (
	ValidStates = []StandardRepresentation{
		// |----------->||----------->||------------->|
		{
			map[DatabaseId]Period{
				1: {start: Date(2021, 1, 1), end: Date(2021, 1, 31)},
				2: {start: Date(2021, 2, 1), end: Date(2021, 2, 28)},
				3: {start: Date(2021, 3, 1), end: Date(2021, 3, 31)},
			},
		},
		{
			map[DatabaseId]Period{
				1: {start: Date(2021, 1, 1), end: Date(2021, 1, 31)},
				2: {start: Date(2021, 2, 1), end: Date(2021, 2, 28)},
				// Whoops! Wrong year, incorrect, but still valid.
				3: {start: Date(2021, 3, 1), end: Date(2210, 3, 31)},
			},
		},
	}

	InvalidStates = []StandardRepresentation{
		// This can be easy to do if you are unsure if the end date is inclusive/exclusive

		// With an overlap
		// |----------->|              |------------->|
		//           |-------------------->|
		{
			map[DatabaseId]Period{
				1: {start: Date(2021, 1, 1), end: Date(2021, 1, 31)},
				2: {start: Date(2021, 1, 25), end: Date(2021, 3, 5)},
				3: {start: Date(2021, 3, 1), end: Date(2021, 3, 31)},
			},
		},
		// With a gap
		// |----------->|  |------>|   |------------->|
		{
			map[DatabaseId]Period{
				1: {start: Date(2021, 1, 1), end: Date(2021, 1, 31)},
				2: {start: Date(2021, 2, 5), end: Date(2021, 2, 25)},
				3: {start: Date(2021, 3, 1), end: Date(2021, 3, 31)},
			},
		},
		// Start after end
		// |----------->||----------->||<-------------|
		{
			map[DatabaseId]Period{
				1: {start: Date(2021, 1, 1), end: Date(2021, 1, 31)},
				2: {start: Date(2021, 2, 5), end: Date(2021, 2, 25)},
				3: {start: Date(2021, 3, 31), end: Date(2021, 3, 1)},
			},
		},
	}
)

func Date(year int, month time.Month, day int) time.Time {
	return time.Date(year, month, day, 0, 0, 0, 0, time.UTC)
}
