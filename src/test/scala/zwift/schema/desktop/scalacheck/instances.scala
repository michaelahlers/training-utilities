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

  private val genPartialWorkoutStep: Gen[(Int, Float, Float)] =
    for {
      durationSeconds <- Gen.posNum[Int].map(_ + 1)
      ftpRatioStart <- Gen.posNum[Float]
      ftpRatioEnd <- Gen.posNum[Float]
    } yield (durationSeconds, ftpRatioStart, ftpRatioEnd)

  val genWarmup: Gen[Warmup] =
    genPartialWorkoutStep.map((Warmup.apply _).tupled)

  implicit val arbWarmup: Arbitrary[Warmup] =
    Arbitrary(genWarmup)

  val genSteadyState: Gen[SteadyState] =
    genPartialWorkoutStep.map { case (durationSeconds, ftpRatioStart, _) =>
      SteadyState(durationSeconds, ftpRatioStart)
    }

  implicit val arbSteadyState: Arbitrary[SteadyState] =
    Arbitrary(genSteadyState)

  val genRamp: Gen[Ramp] =
    genPartialWorkoutStep.map((Ramp.apply _).tupled)

  implicit val arbRamp: Arbitrary[Ramp] =
    Arbitrary(genRamp)

  val genCooldown: Gen[Cooldown] =
    genPartialWorkoutStep.map((Cooldown.apply _).tupled)

  implicit val arbCooldown: Arbitrary[Cooldown] =
    Arbitrary(genCooldown)

  implicit val arbWorkoutStep: Arbitrary[WorkoutStep] =
    ArbitraryDerivation[WorkoutStep]

}
