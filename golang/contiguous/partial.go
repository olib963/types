package contiguous

import "time"

// We can't mess up the order, but at the cost of readability.

type PartialSolution struct {
	d1 time.Time
	d2 time.Time
}

func (p PartialSolution) Start() time.Time {
	if p.d1.Before(p.d2) {
		return p.d1
	}
	return p.d2
}

func (p PartialSolution) End() time.Time {
	if p.d1.After(p.d2) {
		return p.d1
	}
	return p.d2
}
