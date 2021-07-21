package contiguous

import java.time.LocalDate

import contiguous.Arbitraries._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ClosedProjectionSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {
  "The closed projection" should "always contain end dates after start dates" in {
    forAll { contiguous: ContiguousPeriods =>
      val periods = InjectiveStateFunctions.closedProjectionInclusive(contiguous).periods
      periods.foreach { p =>
        withClue(s"For period $p") {
          assert(p.start.isBefore(p.end))
        }
      }
    }
  }

  it should "project into date order" in {
    forAll { contiguous: ContiguousPeriods =>
      val periods = InjectiveStateFunctions.closedProjectionInclusive(contiguous).periods
      zippedWithNext(periods).foreach { case (p, next) =>
        withClue(s"For periods $p and $next") {
          assert(p.start.isBefore(next.start))
        }
      }
    }
  }

  it should "ensure periods always end before the next one starts" in {
    forAll { contiguous: ContiguousPeriods =>
      val periods = InjectiveStateFunctions.closedProjectionInclusive(contiguous).periods
      zippedWithNext(periods).foreach { case (p, next) =>
        withClue(s"For periods $p and $next") {
          assert(p.end.isBefore(next.start))
        }
      }
    }
  }

  it should "always contain all dates between the smallest and largest dates" in {
    forAll(reasonableStateGen) { contiguous =>
      val periods = InjectiveStateFunctions.closedProjectionInclusive(contiguous).periods
      whenever(periods.nonEmpty) {
        val earliest       = contiguous.startDates.min
        val latestMinusOne = contiguous.startDates.max.minusDays(1)
        val allDates       = LazyList.iterate(earliest)(_.plusDays(1)).takeWhile(!_.isAfter(latestMinusOne))
        allDates.foreach { date =>
          withClue(s"For date $date") {
            assert(periods.exists(containsInclusive(_, date)))
          }
        }
      }
    }
  }

  private def containsInclusive(period: Period, date: LocalDate) =
    isBeforeOrEqual(period.start, date) && isAfterOrEqual(period.end, date)

  private def isBeforeOrEqual(d1: LocalDate, d2: LocalDate) = !d1.isAfter(d2)
  private def isAfterOrEqual(d1: LocalDate, d2: LocalDate)  = !d1.isBefore(d2)

  private def zippedWithNext[A](list: List[A]): List[(A, A)] =
    list.zip(list.drop(1))

}
