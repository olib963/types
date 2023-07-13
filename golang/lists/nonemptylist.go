package lists

type NonEmptyList[A any] struct {
	Head A
	Tail []A
}

func NonEmptyOf[A any](head A, tail ...A) NonEmptyList[A] {
	return NonEmptyList[A]{
		Head: head,
		Tail: tail,
	}
}

// No error and is still a total function!
func describeNel(ints NonEmptyList[int]) ListDescription {
	return ListDescription{
		Min:   minNel(ints),
		Max:   maxNel(ints),
		Total: sumNel(ints),
	}
}

func sumNel(ints NonEmptyList[int]) int {
	return NelReduce(ints, func(a int, b int) int {
		return a + b
	})
}

func minNel(ints NonEmptyList[int]) int {
	return NelReduce(ints, func(a int, b int) int {
		if a < b {
			return a
		}
		return b
	})
}

func maxNel(ints NonEmptyList[int]) int {
	return NelReduce(ints, func(a int, b int) int {
		if a > b {
			return a
		}
		return b
	})
}

func NelReduce[A any](nel NonEmptyList[A], f func(A, A) A) A {
	result := nel.Head
	for _, i := range nel.Tail {
		result = f(result, i)
	}
	return result
}

var (
	OnlyValidStates = []NonEmptyList[int]{
		NonEmptyOf(1, 2, 3),
		NonEmptyOf(1, 2, -3), // Valid, but incorrect
	}
	// Even with Golang's 'zero values', NonEmptyList{} is really [0] so you can't create an empty list
)

// Function from new state to old state. Always works
func toSlice[A any](newState NonEmptyList[A]) []A {
	result := make([]A, len(newState.Tail)+1)
	result[0] = newState.Head
	for i, element := range newState.Tail {
		result[i+1] = element
	}
	return result
}

// This strips out invalid states from the old representation, but the validation check only happens once, right here.
func parseNel[A any](slice []A) *NonEmptyList[A] {
	if len(slice) == 0 {
		return nil
	}
	return &NonEmptyList[A]{
		Head: slice[0],
		Tail: slice[1:],
	}
}

func staticClient() {
	description := describeNel(NonEmptyOf(1, 2, 3))
	println(description.ToString()) // Yay! No more errors to deal with
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
		description: describeNel(*nel).ToString(),
	}
}
