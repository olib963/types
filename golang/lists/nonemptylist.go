package lists

type NonEmptyIntList struct {
	Head int
	Tail []int
}

func NonEmptyOf(head int, tail ...int) NonEmptyIntList {
	return NonEmptyIntList{
		Head: head,
		Tail: tail,
	}
}

// No error and is still a total function!
func describeNel(ints NonEmptyIntList) ListDescription {
	return ListDescription{
		Min:   minNel(ints),
		Max:   maxNel(ints),
		Total: sumNel(ints),
	}
}

func sumNel(ints NonEmptyIntList) int {
	return NelReduce(ints, func(a int, b int) int {
		return a + b
	})
}

func minNel(ints NonEmptyIntList) int {
	return NelReduce(ints, func(a int, b int) int {
		if a < b {
			return a
		}
		return b
	})
}

func maxNel(ints NonEmptyIntList) int {
	return NelReduce(ints, func(a int, b int) int {
		if a > b {
			return a
		}
		return b
	})
}

func NelReduce(ints NonEmptyIntList, f func(int, int) int) int {
	result := ints.Head
	for _, i := range ints.Tail {
		result = f(result, i)
	}
	return result
}

var (
	OnlyValidStates = []NonEmptyIntList{
		NonEmptyOf(1, 2, 3),
		NonEmptyOf(1, 2, -3), // Valid, but incorrect
	}
	// Even with Golang's 'zero values', NonEmptyIntList{} is really [0] so you can't create an empty list
)

// Function from new state to old state. Always works
func toSlice(newState NonEmptyIntList) []int {
	result := make([]int, len(newState.Tail)+1)
	result[0] = newState.Head
	for i, element := range newState.Tail {
		result[i+1] = element
	}
	return result
}

// This strips out invalid states from the old representation, but the validation check only happens once, right here.
func parseNel(ints []int) *NonEmptyIntList {
	if len(ints) == 0 {
		return nil
	}
	return &NonEmptyIntList{
		Head: ints[0],
		Tail: ints[1:],
	}
}

func staticClient() {
	description := describeNel(NonEmptyOf(1, 2, 3))
	println(description.String()) // Yay! No more errors to deal with
}

type Request struct {
	Ints []int
}

type Response struct {
	code        int
	description string
}

// Pretend RPC client
func rpcClient(request Request) Response {
	nel := parseNel(request.Ints)
	if nel == nil {
		return Response{code: 400, description: "List cannot be empty"}
	}
	return Response{
		code:        200,
		description: describeNel(*nel).String(),
	}
}
