package survey

case class FirstAttempt(
  questions: List[String],
  answers: List[Option[String]],
  current: Int, // Is this zero or one indexed?
)

object FirstAttempt {

  def answerQuestion(survey: FirstAttempt, answer: String): FirstAttempt = FirstAttempt(
    questions = survey.questions,
    answers = survey.answers.updated(survey.current, Some(answer)), // Is this a safe update?? It _should_ be
    current = survey.current,
  )

  def forwards(survey: FirstAttempt): FirstAttempt = FirstAttempt(
    questions = survey.questions,
    answers = survey.answers,
    // Should it be the size of question or answers? They _should_ be the same right?
    current = (survey.current + 1).min(survey.questions.size),
  )

  def back(survey: FirstAttempt): FirstAttempt = FirstAttempt(
    questions = survey.questions,
    answers = survey.answers,
    current = (survey.current - 1).max(1), // Is this max 1 or 0?
  )

  def printAll(survey: FirstAttempt): Unit =
    survey.questions
      .zip(survey.answers)
      .foreach { case (q, a) =>
        println(s"$q: ${a.getOrElse("")}")
      }
}

object InvalidStates {
  val noQuestions = FirstAttempt(
    questions = List.empty,
    answers = List.empty,
    current = 0,
  )

  val moreQuestionsThanAnswers = FirstAttempt(
    questions = List("What's your name?", "What's your favourite colour?"),
    answers = List(Some("Oli")), // There should be a None in the second index!
    current = 1,
  )

  val moreAnswersThanQuestions = FirstAttempt(
    questions = List("What's your name?"),
    answers = List(Some("Oli"), Some("Hello")), // What does the second answer correspond to?
    current = 1,
  )

  val wrongIndex = FirstAttempt(
    questions = List("What's your name?", "What's your favourite colour?"),
    answers = List(None, None),
    current = 3, // What is question 3??
  )

}

object ValidStates {
  val correct = FirstAttempt(
    questions = List("What's your name?"),
    answers = List(None),
    current = 0,
  )

  val incorrect = FirstAttempt(
    questions = List("What's your name?", "What's your favourite colour?"),
    answers = List(Some("Red"), None), // Answered the wrong question
    current = 0,
  )
}
