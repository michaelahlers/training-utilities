package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.NoWorkoutsForStep
import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.diffx.instances._
import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.diffx.instances._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks._
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import trainerroad.schema.web.diffx.instances._
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.diffx.instances._
import zwift.schema.desktop.scalacheck.instances._

class ToWorkoutStepsSpec extends AnyWordSpec {
  import ToWorkoutStepsSpec.arbSteps
  import ToWorkoutStepsSpec.toWorkoutData

  "No workouts for interval" when {

    "workouts are empty" in {

      ToWorkoutStep
        .from(
          workouts = Seq.empty,
        )
        .shouldMatchTo(Invalid(NoWorkoutsForStep))
    }

  }

  "Warmup" when {

    "first interval" in {

      forAll(sizeRange(5)) { steps: NonEmptyList[WorkoutStep] =>
        val workouts = toWorkoutData(steps).toList

        ToWorkoutSteps
          .from(
            workouts = workouts,
          )
          .shouldMatchTo(Valid(steps.toList))
      }
    }

  }

}

object ToWorkoutStepsSpec {

  def toFtpRatios(
    step: WorkoutStep,
    isLast: Boolean,
  ): NonEmptyList[Float] = {
    import step.{ftpRatioEnd, ftpRatioStart}

    /**
     * The number of step slices (each [[WorkoutData]] is equal to the duration in seconds of [[step]].
     * However, there's a special case as [[Workout.workoutData]] includes "terminator" slice with the final power.
     */
    val slices =
      if (isLast) 0 to step.durationSeconds
      else 0 until step.durationSeconds

    val ftpDelta: Float = ftpRatioEnd - ftpRatioStart
    val ftpStep: Float =
      if (slices.size < 2) ftpDelta
      else ftpDelta / (slices.size - 1)

    val ratios = slices.map { slice =>
      ftpRatioStart + ftpStep * slice
    }

    NonEmptyList.fromListUnsafe(ratios.toList)
  }

  def toWorkoutData(
    step: WorkoutStep,
    offsetSeconds: Int,
    isLast: Boolean,
  ): NonEmptyList[WorkoutData] = {
    val ratios: NonEmptyList[Float] = toFtpRatios(
      step = step,
      isLast = isLast,
    )

    ratios
      .zipWithIndex
      .map { case (ftpRatio, slice) =>
        WorkoutData(
          milliseconds = (offsetSeconds + slice) * 1000,
          memberFtpPercent = 0,
          ftpPercent = ftpRatio * 100,
        )
      }
  }

  def toWorkoutData(
    steps: NonEmptyList[WorkoutStep],
    offsetSeconds: Int,
  ): NonEmptyList[WorkoutData] = {
    val lastIndex = steps.size - 1

    /** Not efficient, but that's fine. */
    val ratios: NonEmptyList[Float] = steps
      .zipWithIndex
      .flatMap { case (step, index) =>
        val isLast = lastIndex == index
        toFtpRatios(
          step = step,
          isLast = isLast,
        )
      }

    ratios
      .zipWithIndex
      .map { case (ftpRatio, slice) =>
        WorkoutData(
          milliseconds = (offsetSeconds + slice) * 1000,
          memberFtpPercent = 0,
          ftpPercent = ftpRatio * 100,
        )
      }
  }

  def toWorkoutData(
    steps: NonEmptyList[WorkoutStep],
  ): NonEmptyList[WorkoutData] =
    toWorkoutData(
      steps = steps,
      offsetSeconds = 0,
    )

  /**
   */
  implicit val arbSteps: Arbitrary[NonEmptyList[WorkoutStep]] = Arbitrary {

    /**
     * An interior [[WorkoutStep]] can only be a [[WorkoutStep.SteadyState]] or [[WorkoutStep.Ramp]].
     * [[WorkoutStep.Warmup]] and [[WorkoutStep.Cooldown]] are always the first and last steps, respectively.
     */
    val genInterior: Gen[WorkoutStep] = Gen.oneOf(
      genSteadyState,
      genRamp,
    )

    for {
      warmup <- genWarmup
      interior <- Gen.listOf(genInterior)
      cooldown <- genCooldown
    } yield NonEmptyList.one(warmup) ++ interior :+ cooldown
  }

}
