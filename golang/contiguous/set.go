package contiguous

import "time"

// Golang does not have a set implementation so I have written one here
type DateSet struct {
	internal map[time.Time]struct{}
}

func SetOf(dates ...time.Time) DateSet {
	set := DateSet{internal: make(map[time.Time]struct{})}
	for _, date := range dates {
		set = set.Add(date)
	}
	return set
}

func (set DateSet) Add(date time.Time) DateSet {
	c := set.copy()
	c.internal[date] = struct{}{}
	return c
}

func (set DateSet) Remove(date time.Time) DateSet {
	c := set.copy()
	delete(c.internal, date)
	return c
}

func (set DateSet) Contains(date time.Time) bool {
	_, exists := set.internal[date]
	return exists
}

func (set DateSet) toSlice() []time.Time {
	s := make([]time.Time, len(set.internal))
	index := 0
	for date := range set.internal {
		s[index] = date
		index++
	}
	return s
}

func (set DateSet) copy() DateSet {
	c := make(map[time.Time]struct{})
	for k, v := range set.internal {
		c[k] = v
	}
	return DateSet{
		internal: c,
	}
}
