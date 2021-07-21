package contiguous

import java.time.LocalDate

import contiguous.Arbitraries._
import contiguous.InjectiveStateFunctions.{OpenPeriod, OpenProjection}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class OpenProjectionSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks {
  "The open projection" should "always contain end dates after start dates" in {
    forAll { contiguous: ContiguousPeriods =>
      val periods = InjectiveStateFunctions.openProjectionExclusive(contiguous).closedPeriods
      periods.foreach { p =>
        withClue(s"For period $p") {
          assert(p.start.isBefore(p.end))
        }
      }
    }
  }

  it should "project into date order" in {
    forAll { contiguous: ContiguousPeriods =>
      val periods = InjectiveStateFunctions.openProjectionExclusive(contiguous).closedPeriods
      zippedWithNext(periods).foreach { case (p, next) =>
        withClue(s"For periods $p and $next") {
          assert(p.start.isBefore(next.start))
        }
      }
    }
  }

  it should "ensure periods always end at the same time the next one starts" in {
    forAll { contiguous: ContiguousPeriods =>
      val periods = InjectiveStateFunctions.openProjectionExclusive(contiguous).closedPeriods
      zippedWithNext(periods).foreach { case (p, next) =>
        withClue(s"For periods $p and $next") {
          assert(p.end === next.start)
        }
      }
    }
  }

  it should "always contain all dates between the smallest and largest dates" in {
    forAll(reasonableStateGen) { contiguous =>
      val periods = InjectiveStateFunctions.openProjectionExclusive(contiguous).closedPeriods
      whenever(periods.nonEmpty) {
        val earliest       = contiguous.startDates.min
        val latestMinusOne = contiguous.startDates.max.minusDays(1)
        val allDates       = LazyList.iterate(earliest)(_.plusDays(1)).takeWhile(!_.isAfter(latestMinusOne))
        allDates.foreach { date =>
          withClue(s"For date $date") {
            assert(periods.exists(containsExclusive(_, date)))
          }
        }
      }
    }
  }

  it should "always leave the latest date as an open period" in {
    forAll { contiguous: ContiguousPeriods =>
      whenever(contiguous.startDates.nonEmpty) {
        val current    = InjectiveStateFunctions.openProjectionExclusive(contiguous).current
        val latestDate = contiguous.startDates.max
        assert(current == Option(OpenPeriod(latestDate)))
      }
    }
  }

  it should "handle the empty state" in {
    val projection = InjectiveStateFunctions.openProjectionExclusive(ContiguousPeriods(Set()))
    assert(projection == OpenProjection(closedPeriods = List.empty, current = Option.empty))
  }

  private def zippedWithNext[A](list: List[A]): List[(A, A)] =
    list.zip(list.drop(1))

  private def containsExclusive(period: Period, date: LocalDate) =
    isBeforeOrEqual(period.start, date) && period.end.isAfter(date)
  private def isBeforeOrEqual(d1: LocalDate, d2: LocalDate) = !d1.isAfter(d2)

}
