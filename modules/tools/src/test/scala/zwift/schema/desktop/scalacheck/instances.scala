package zwift.schema.desktop.scalacheck

import magnolify.scalacheck.semiauto._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import squants.time.Seconds
import squants.time.Time
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

object instances {

  implicit private val arbDuration: Arbitrary[Time] = Arbitrary(Gen
    .posNum[Int]
    .map(_ + 1)
    .map(Seconds(_)))

  /**
   * Steady-steady or increasing.
   */
  val genWarmup: Gen[Warmup] =
    for {
      duration <- arbitrary[Time]
      ftpPercentStart <- Gen.choose(45, 65)
      ftpPercentDelta <- Gen.choose(0, 50)
    } yield {
      val ftpPercentEnd = ftpPercentStart + ftpPercentDelta
      Warmup(duration, ftpPercentStart, ftpPercentDelta)
    }

  implicit val arbWarmup: Arbitrary[Warmup] =
    Arbitrary(genWarmup)

  val genSteadyState: Gen[SteadyState] =
    for {
      duration <- arbitrary[Time]
      ftpPercentStart <- Gen.choose(75, 125)
    } yield SteadyState(duration, ftpPercentStart)

  implicit val arbSteadyState: Arbitrary[SteadyState] =
    Arbitrary(genSteadyState)

  val genRampIncreasing: Gen[Ramp] =
    for {
      duration <- arbitrary[Time]
      ftpPercentStart <- Gen.choose(50, 75)
      ftpPercentDelta <- Gen.choose(25, 50)
    } yield {
      val ftpPercentEnd = ftpPercentStart + ftpPercentDelta
      Ramp(duration, ftpPercentStart, ftpPercentEnd)
    }

  val genRampDecreasing: Gen[Ramp] =
    for {
      duration <- arbitrary[Time]
      ftpPercentDelta <- Gen.choose(25, 50)
      ftpPercentEnd <- Gen.choose(50, 75)
    } yield {
      val ftpPercentStart = ftpPercentDelta + ftpPercentEnd
      Ramp(duration, ftpPercentStart, ftpPercentEnd)
    }

  /** Increasing or decreasing, but never steady-state. */
  val genRamp: Gen[Ramp] = Gen.oneOf(
    genRampIncreasing,
    genRampDecreasing,
  )

  implicit val arbRamp: Arbitrary[Ramp] =
    Arbitrary(genRamp)

  /**
   * Steady-steady or decreasing.
   */
  val genCooldown: Gen[Cooldown] =
    for {
      duration <- arbitrary[Time]
      ftpPercentDelta <- Gen.choose(45, 65)
      ftpPercentEnd <- Gen.posNum[Int]
    } yield {
      val ftpPercentStart = ftpPercentDelta + ftpPercentEnd
      Cooldown(duration, ftpPercentStart, ftpPercentEnd)
    }

  implicit val arbCooldown: Arbitrary[Cooldown] =
    Arbitrary(genCooldown)

  implicit val arbWorkoutStep: Arbitrary[WorkoutStep] =
    ArbitraryDerivation[WorkoutStep]

}
