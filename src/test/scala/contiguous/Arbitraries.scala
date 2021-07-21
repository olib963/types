package contiguous

import java.time.LocalDate

import org.scalacheck.{Arbitrary, Gen}

object Arbitraries {
  implicit val arbState: Arbitrary[ContiguousPeriods] = Arbitrary(Gen.resultOf(ContiguousPeriods(_)))

  /*
   * A restriction on the dates used to be within a "reasonable" range, this is just to avoid some tests
   * Generating two vastly different dates e.g. LocalDate.of(-2000000, 1, 1) and LocalDate.of(2000000, 1, 1)
   * This would cause the tests to run for a _very_ long time to check all dates between the two.
   */
  private val reasonableDateGen: Gen[LocalDate] =
    Gen.chooseNum(LocalDate.of(1900, 1, 1).toEpochDay, LocalDate.of(2100, 12, 31).toEpochDay).map(LocalDate.ofEpochDay)
  val reasonableStateGen: Gen[ContiguousPeriods] =
    Gen.listOf(reasonableDateGen).map(dates => ContiguousPeriods(dates.toSet))

}
