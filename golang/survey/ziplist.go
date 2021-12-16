package survey

import "fmt"

type ZipListQuestions struct {
	previous []Question
	current  Question
	next     []Question
}

type Question struct {
	question string
	answer   *string
}

type Survey = ZipListQuestions

func SurveyOf(firstQuestion string, others ...string) Survey {
	next := make([]Question, len(others))
	for i, q := range others {
		next[i] = Question{
			question: q,
			answer:   nil,
		}
	}
	return ZipListQuestions{
		previous: make([]Question, 0),
		current: Question{
			question: firstQuestion,
			answer:   nil,
		},
		next: next,
	}
}

func answerQuestionZip(survey Survey, answer string) Survey {
	survey.current.answer = &answer
	return survey
}

// The following are easier to implement in pattern matching
func forwardsZip(survey Survey) Survey {
	if len(survey.next) == 0 {
		return survey
	}
	// Move current question into the previous list
	previous := append(survey.previous, survey.current)
	return ZipListQuestions{
		previous: previous,
		current:  survey.next[0],
		next:     survey.next[1:],
	}
}

func backZip(survey Survey) Survey {
	if len(survey.previous) == 0 {
		return survey
	}
	// Move the current question into the next list
	next := append([]Question{survey.current}, survey.next...)
	return ZipListQuestions{
		previous: survey.previous[:len(survey.previous)],
		current:  survey.previous[len(survey.previous)],
		next:     next,
	}
}

func printAllZip(survey Survey) {
	for i, question := range toList(survey) {
		answer := ""
		if question.answer != nil {
			answer = *question.answer
		}
		number := i + 1
		println(fmt.Sprintf("Question %v: %s: %v", number, question.question, answer))
	}
}

var (
	// Only Valid States
	initial           = SurveyOf("What's your name?", "What's your favourite colour?")
	correct           = answerQuestionZip(initial, "Oli")
	answerIncorrectly = func() Survey {
		firstAnswered := answerQuestionZip(initial, "Red")
		secondQuestion := forwardsZip(firstAnswered)
		return answerQuestionZip(secondQuestion, "Oli")
	}
	incorrect = answerIncorrectly()
)

func toList(survey Survey) []Question {
	questions := survey.previous
	questions = append(questions, survey.current)
	questions = append(questions, survey.next...)
	return questions
}

func toSeparated(survey Survey) SeparatedSurvey {
	questions := toList(survey)
	qs := make([]string, len(questions))
	as := make([]*string, len(questions))
	for i, question := range questions {
		qs[i] = question.question
		as[i] = question.answer
	}
	return SeparatedSurvey{
		questions: qs,
		answers:   as,
		current:   len(survey.previous),
	}
}
