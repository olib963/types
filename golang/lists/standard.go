package lists

import "errors"

func Describe(ints []int) ListDescription {
	return ListDescription{
		Min:   min(ints),
		Max:   max(ints),
		Total: sum(ints),
	}
}

func sum(ints []int) int {
	total := 0
	for _, i := range ints {
		total += i
	}
	return total
}

func min(ints []int) int {
	if len(ints) == 0 {
		panic("Empty lists are not allowed!")
	}
	m := ints[0]
	for _, i := range ints {
		if i < m {
			m = i
		}
	}
	return m
}

func max(ints []int) int {
	if len(ints) == 0 {
		panic("Empty lists are not allowed!")
	}
	m := ints[0]
	for _, i := range ints {
		if i > m {
			m = i
		}
	}
	return m
}

var (
	ValidStates = [][]int{
		{1, 2, 3},  // correct
		{1, 2, -3}, // Accidentally typed in the wrong number of -3. Valid but incorrect.
	}
	InvalidStates = [][]int{
		// It's possible to represent empty lists, so they have to be "handled" everywhere.
		// Above that's done by panicking
		{},
	}
)

/*A common approach to solving this is to make each function total by handling the invalid states and returning
 * some "error value" for these cases.
 *
 * N.B. Total functions are functions that are defined for all of their possible input values. Partial functions
 * are only defined on a subset of input values.
 */

func DescribeError(ints []int) (ListDescription, error) {
	min, err := minError(ints)
	if err != nil {
		return ListDescription{}, err
	}

	max, err := maxError(ints)
	if err != nil {
		// err will _never_ not be nil here, because we already checked it when we did minError!
		return ListDescription{}, err
	}
	return ListDescription{
		Min:   min,
		Max:   max,
		Total: sum(ints), // Sum is already a total function, so is fine to reuse
	}, nil
}

// Our min and max functions can now be implemented, but they pass the problem up the call chain
func minError(ints []int) (int, error) {
	if len(ints) == 0 {
		return 0, errors.New("Empty lists are not allowed!")
	}
	m := ints[0]
	for _, i := range ints {
		if i < m {
			m = i
		}
	}
	return m, nil
}

// We now have a shotgun validation problem, every function is validating the same things because they can't tell
// what was previously checked.
func maxError(ints []int) (int, error) {
	if len(ints) == 0 {
		return 0, errors.New("Empty lists are not allowed!")
	}
	m := ints[0]
	for _, i := range ints {
		if i > m {
			m = i
		}
	}
	return m, nil
}

// The burden is further passed up to our clients!
func useAPI() {
	description, err := DescribeError([]int{1, 2, 3})
	if err != nil {
		panic("I already know my list isn't empty! Why do I need to check this??")
	}
	println(description.ToString())
}
