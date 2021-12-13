package survey

import lists.NonEmptyList

case class ZipList[A](previous: List[A], current: A, next: List[A])

object ZipList {
  def fromNonEmpty[A](list: NonEmptyList[A]): ZipList[A] =
    ZipList(previous = List.empty, current = list.head, next = list.tail)

  def toList[A](zipList: ZipList[A]): List[A] =
    zipList.previous ++ (zipList.current :: zipList.next)
}

case class Question(question: String, answer: Option[String])

object Survey {
  type Survey = ZipList[Question]
  def of(questions: NonEmptyList[String]): Survey =
    ZipList.fromNonEmpty(questions.map(Question(_, answer = None)))

  def answerQuestion(survey: Survey, answer: String): Survey = {
    val currentWithAnswer = survey.current.copy(answer = Some(answer))
    survey.copy(current = currentWithAnswer)
  }

  def forwards(survey: Survey): Survey = survey.next match {
    case Nil => survey // No more questions
    case nextQuestion :: remainingQuestions =>
      ZipList(
        previous = survey.previous.appended(survey.current),
        current = nextQuestion,
        next = remainingQuestions,
      )
  }

  def back(survey: Survey): Survey = survey.previous.lastOption match {
    case None => survey // No previous questions
    case Some(previousQuestion) =>
      ZipList(
        previous = survey.previous.init,
        current = previousQuestion,
        next = survey.current :: survey.next,
      )
  }

  def printAll(survey: Survey): Unit =
    ZipList
      .toList(survey)
      .foreach(question => println(s"${question.question}: ${question.answer.getOrElse("")}"))
}

object OnlyValidStates {
  private val initial =
    Survey.of(NonEmptyList("What's your name?", "What's your favourite colour?"))

  val correct = Survey.answerQuestion(initial, "Oli")

  // Answered the wrong question
  val incorrect = Survey.answerQuestion(initial, "Red")
}

object InjectiveStateFunction {
  import survey.Survey.Survey
  def toSeparated(survey: Survey): SeparatedSurvey = {
    val questions = ZipList.toList(survey)
    SeparatedSurvey(
      questions = questions.map(_.question),
      answers = questions.map(_.answer),
      current = survey.previous.size,
    )
  }
}
