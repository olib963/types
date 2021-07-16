package survey

case class SeparatedSurvey(
  questions: List[String],
  answers: List[Option[String]],
  current: Int,
)

object SeparatedSurvey {

  def answerQuestion(survey: SeparatedSurvey, answer: String): SeparatedSurvey =
    survey.copy(
      answers = survey.answers.updated(survey.current, Some(answer)),
    )

  def forwards(survey: SeparatedSurvey): SeparatedSurvey =
    survey.copy(
      current = survey.current + 1,
    )

  def back(survey: SeparatedSurvey): SeparatedSurvey =
    survey.copy(
      current = survey.current - 1,
    )

  def printAll(survey: SeparatedSurvey): Unit =
    survey.questions
      .zip(survey.answers) // These may not zip well if there are not the same number of questions and answers
      .foreach { case (q, a) =>
        println(s"$q: ${a.getOrElse("")}")
      }
}

object ValidStates {
  val correct = SeparatedSurvey(
    questions = List("What's your name?"),
    answers = List(None),
    current = 0,
  )

  // Answered the wrong question
  val incorrect = SeparatedSurvey(
    questions = List("What's your name?", "What's your favourite colour?"),
    answers = List(Some("Red"), Some("Oli")),
    current = 0,
  )
}

object InvalidStates {
  val noQuestions = SeparatedSurvey(
    questions = List.empty,
    answers = List.empty,
    current = 0,
  )

  val moreQuestionsThanAnswers = SeparatedSurvey(
    questions = List("What's your name?", "What's your favourite colour?"),
    answers = List(Some("Oli")),
    current = 1,
  )

  val moreAnswersThanQuestions = SeparatedSurvey(
    questions = List("What's your name?"),
    answers = List(Some("Oli"), Some("Hello")),
    current = 1,
  )

  val wrongIndex = SeparatedSurvey(
    questions = List("What's your name?", "What's your favourite colour?"),
    answers = List(None, None),
    current = 3,
  )

}

/* For the invalid state cases, if we want our code to be safe we should update our functions to
 * handle the invalid states.
 */
object Safety {

  def safeAnswerQuestion(survey: SeparatedSurvey, answer: String): SeparatedSurvey =
    try survey.copy(
      answers = survey.answers.updated(survey.current, Some(answer)),
    )
    catch {
      // "Handle" the case where the curent index is invalid
      case indexOutOfBoundsException: IndexOutOfBoundsException =>
        survey
    }

  // It's hard to make sure we are incrementing the index correctly. Is it capped by the number
  // of questions or answers? They _should_ be the same right?
  def safeForwards(survey: SeparatedSurvey): SeparatedSurvey = {
    val expectedIndex = survey.current + 1
    val maximumIndex  = survey.questions.size
    survey.copy(
      current = expectedIndex.min(maximumIndex),
    )
  }

  def safeBackwards(survey: SeparatedSurvey): SeparatedSurvey = {
    val expectedIndex = survey.current - 1
    survey.copy(
      current = expectedIndex.max(0), // Expecting 0 indexed lists
    )
  }

}
