package survey

import (
	"golang/lists"
)

// This gets us most of the way there - we can't have:
// An empty list of questions or a mismatch between the number of questions and answers
//
// We can still have:
// A pointer to a question that doesn't exist

type Question struct {
	question string
	answer   *string
}

type PartialSolution struct {
	questions lists.NonEmptyList[Question]
	current   int
}
