package contiguous

import (
	"errors"
	"fmt"
	"sort"
	"time"
)

// Reducing data redundancy here means that we cannot represent gaps or overlaps.
type ContiguousPeriods struct {
	startDates DateSet
}

func createNewPeriodContiguous(contiguousPeriods ContiguousPeriods, date time.Time) ContiguousPeriods {
	return ContiguousPeriods{
		startDates: contiguousPeriods.startDates.Add(date),
	}
}

func updatePeriodContiguous(contiguousPeriods ContiguousPeriods, oldStart time.Time, newStart time.Time) (ContiguousPeriods, error) {
	if !contiguousPeriods.startDates.Contains(oldStart) {
		return contiguousPeriods, errors.New(fmt.Sprintf("No period exists starting on %v", oldStart))
	}
	return ContiguousPeriods{
		startDates: contiguousPeriods.startDates.Remove(oldStart).Add(newStart),
	}, nil
}

var (
	OnlyValidStates = []ContiguousPeriods{
		// Just representing the start dates
		// |           |           |
		{
			startDates: SetOf(
				// The order does not matter!
				Date(2021, 1, 1),
				Date(2021, 3, 1),
				Date(2021, 2, 1),
			),
		},
		{
			startDates: SetOf(
				Date(2021, 1, 1),
				Date(2210, 3, 1), // Whoops! Wrong year
				Date(2021, 2, 1),
			),
		},
	}
)

type ClosedProjection struct {
	periods []Period
}

func ClosedProjectionInclusive(contiguousPeriods ContiguousPeriods) ClosedProjection {
	dates := zipWithNext(sorted(contiguousPeriods))
	periods := make([]Period, 0)
	for _, tuple := range dates {
		periods = append(periods, Period{
			start: tuple.first,
			end:   tuple.second.Add(-24 * time.Hour),
		})
	}
	return ClosedProjection{periods: periods}
}

type OpenPeriod struct {
	start time.Time
}

type OpenProjection struct {
	closedPeriods []Period
	current       *OpenPeriod
}

func OpenProjectionExclusive(contiguousPeriods ContiguousPeriods) OpenProjection {

	sortedDates := sorted(contiguousPeriods)

	dates := zipWithNext(sortedDates)
	periods := make([]Period, 0)
	for _, tuple := range dates {
		periods = append(periods, Period{
			start: tuple.first,
			end:   tuple.second,
		})
	}

	var currentPeriod *OpenPeriod
	if len(sortedDates) > 0 {
		currentPeriod = &OpenPeriod{
			start: sortedDates[len(sortedDates)-1],
		}
	}
	return OpenProjection{closedPeriods: periods, current: currentPeriod}
}

func sorted(contiguousPeriods ContiguousPeriods) []time.Time {
	slice := contiguousPeriods.startDates.toSlice()
	sort.Slice(slice, func(i, j int) bool {
		return slice[i].Before(slice[j])
	})
	return slice
}

type datetuple struct {
	first  time.Time
	second time.Time
}

func zipWithNext(dates []time.Time) []datetuple {
	tuples := make([]datetuple, 0)
	for i, date := range dates[:len(dates)-1] {
		tuples = append(tuples, datetuple{
			first:  date,
			second: dates[i+1],
		})
	}
	return tuples
}
