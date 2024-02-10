package zwift.schema.desktop.scalacheck

import magnolify.scalacheck.semiauto._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

object instances {

  /**
   * Steady-steady or increasing.
   */
  val genWarmup: Gen[Warmup] =
    for {
      durationSeconds <- Gen.posNum[Int].map(_ + 1)
      ftpPercentStart <- Gen.posNum[Int]
      ftpPercentDelta <- Gen.choose(0, 50)
    } yield {
      val ftpPercentEnd = ftpPercentStart + ftpPercentDelta
      val ftpRatioStart = ftpPercentStart / 100f
      val ftpRatioEnd = ftpPercentEnd / 100f
      Warmup(durationSeconds, ftpRatioStart, ftpRatioEnd)
    }

  implicit val arbWarmup: Arbitrary[Warmup] =
    Arbitrary(genWarmup)

  val genSteadyState: Gen[SteadyState] =
    for {
      durationSeconds <- Gen.posNum[Int].map(_ + 1)
      ftpPercent <- Gen.posNum[Int]
    } yield {
      val ftpRatio = ftpPercent / 100f
      SteadyState(durationSeconds, ftpRatio)
    }

  implicit val arbSteadyState: Arbitrary[SteadyState] =
    Arbitrary(genSteadyState)

  val genRampIncreasing: Gen[Ramp] =
    for {
      durationSeconds <- Gen.posNum[Int].map(_ + 1)
      ftpPercentStart <- Gen.posNum[Int]
      ftpPercentDelta <- Gen.choose(10, 20)
    } yield {
      val ftpPercentEnd = ftpPercentStart + ftpPercentDelta
      val ftpRatioStart = ftpPercentStart / 100f
      val ftpRatioEnd = ftpPercentEnd / 100f
      Ramp(durationSeconds, ftpRatioStart, ftpRatioEnd)
    }

  val genRampDecreasing: Gen[Ramp] =
    for {
      durationSeconds <- Gen.posNum[Int].map(_ + 1)
      ftpPercentDelta <- Gen.choose(10, 20)
      ftpPercentEnd <- Gen.posNum[Int]
    } yield {
      val ftpPercentStart = ftpPercentDelta + ftpPercentEnd
      val ftpRatioStart = ftpPercentStart / 100f
      val ftpRatioEnd = ftpPercentEnd / 100f
      Ramp(durationSeconds, ftpRatioStart, ftpRatioEnd)
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
      durationSeconds <- Gen.posNum[Int].map(_ + 1)
      ftpPercentDelta <- Gen.choose(0, 50)
      ftpPercentEnd <- Gen.posNum[Int]
    } yield {
      val ftpPercentStart = ftpPercentDelta + ftpPercentEnd
      val ftpRatioStart = ftpPercentStart / 100f
      val ftpRatioEnd = ftpPercentEnd / 100f
      Cooldown(durationSeconds, ftpRatioStart, ftpRatioEnd)
    }

  implicit val arbCooldown: Arbitrary[Cooldown] =
    Arbitrary(genCooldown)

  implicit val arbWorkoutStep: Arbitrary[WorkoutStep] =
    ArbitraryDerivation[WorkoutStep]

}
