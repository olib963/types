package survey

import "fmt"

type SeparatedSurvey struct {
	questions []string
	answers   []*string
	current   int
}

func answerQuestion(survey SeparatedSurvey, answer string) {
	survey.answers[survey.current] = &answer
}

func forwards(survey SeparatedSurvey) {
	survey.current = survey.current + 1
}
func back(survey SeparatedSurvey) {
	survey.current = survey.current - 1
}

func printAll(survey SeparatedSurvey) {
	for i, question := range survey.questions {
		answer := survey.answers[i]
		number := i + 1
		println(fmt.Sprintf("Question %v: %s: %v"), number, question, answer)
	}
}

var (
	red         = "Red"
	oli         = "Oli"
	ValidStates = []SeparatedSurvey{
		{
			questions: []string{"What's your name?"},
			answers:   make([]*string, 1),
			current:   0,
		},

		// Answered the wrong questions
		{
			questions: []string{"What's your name?", "What's your favourite colour?"},
			answers:   []*string{&red, &oli},
			current:   1,
		},
	}
	InvalidStates = []SeparatedSurvey{
		// No questions
		{
			questions: make([]string, 0),
			answers:   make([]*string, 0),
			current:   0,
		},
		// Missing answer slot, can't answer the second question
		{
			questions: []string{"What's your name?", "What's your favourite colour?"},
			answers:   make([]*string, 1),
			current:   0,
		},
		// More answers than questions
		{
			questions: []string{"What's your name?"},
			answers:   []*string{&oli, &red},
			current:   0,
		},
		// Question with index 2 does not exist! we went too far forward
		{
			questions: []string{"What's your name?", "What's your favourite colour?"},
			answers:   make([]*string, 2),
			current:   2,
		},
	}
)

/* For the invalid state cases, if we want our code to be safe we should update our functions to
 * handle the invalid states.
 */

func safeAnswerQuestion(survey SeparatedSurvey, answer string) {
	// "Handle" the case where the curent index is invalid
	if survey.current < len(survey.questions) {
		survey.answers[survey.current] = &answer
		// Is this safe? Do we know len(questions) = len(answers)? It _should_ be
	}
}

// It's hard to make sure we are incrementing the index correctly. Is it capped by the number
// of questions or answers? They _should_ be the same right?
func safeForwards(survey SeparatedSurvey) {
	expectedIndex := survey.current + 1
	maximumIndex := len(survey.questions)
	if expectedIndex < maximumIndex {
		survey.current = expectedIndex
	}
}

func safeBackwards(survey SeparatedSurvey) {
	expectedIndex := survey.current - 1
	minimumIndex := 0
	if expectedIndex >= minimumIndex {
		survey.current = expectedIndex
	}
}
