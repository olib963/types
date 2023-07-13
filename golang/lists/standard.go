package lists

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
	for _, i := range ints[1:] {
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
	for _, i := range ints[1:] {
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

func DescribeError(ints []int) (ListDescription, bool) {
	min, ok := tryMin(ints)
	if !ok {
		return ListDescription{}, ok
	}

	max, ok := tryMax(ints)
	if !ok {
		// We will _never_ not be here, because we already checked
		// it when we did tryMin!
		return ListDescription{}, ok
	}
	return ListDescription{
		Min:   min,
		Max:   max,
		Total: sum(ints), // Sum is already a total function, so is fine to reuse
	}, true
}

// Our min and max functions can now be implemented,
// but they pass the problem up the call chain
func tryMin(ints []int) (int, bool) {
	if len(ints) == 0 {
		return 0, false
	}
	m := ints[0]
	for _, i := range ints[1:] {
		if i < m {
			m = i
		}
	}
	return m, true
}

// We now have a shotgun validation problem, every function is validating the same things because they can't tell
// what was previously checked.
func tryMax(ints []int) (int, bool) {
	if len(ints) == 0 {
		return 0, false
	}
	m := ints[0]
	for _, i := range ints[1:] {
		if i > m {
			m = i
		}
	}
	return m, true
}

// The burden is further passed up to our clients!
func useAPI() {
	description, ok := DescribeError([]int{1, 2, 3})
	if !ok {
		panic("I already know my list isn't empty! Why do I need to check this??")
	}
	println(description.ToString())
}
