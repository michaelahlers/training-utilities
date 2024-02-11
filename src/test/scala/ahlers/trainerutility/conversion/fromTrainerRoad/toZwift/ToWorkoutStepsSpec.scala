package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.NoIntervalsInWorkout
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
import squants.Seconds
import squants.time.Time
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.diffx.instances._
import zwift.schema.desktop.scalacheck.instances._

/**
 * Overall structure informs special cases of Zwift workouts, specifically the warmup and cooldown intervals.
 * In that light, there's little logic to tests that don't verify the workout in its entireity.
 */
class ToWorkoutStepsSpec extends AnyWordSpec {
  import ToWorkoutStepsSpec.arbSteps
  import ToWorkoutStepsSpec.toWorkoutData

  "Invalid workout steps" when {

    "no intervals in given workout" in {
      val workouts: NonEmptyList[WorkoutData] = NonEmptyList.one(WorkoutData(null, 0, 0))

      ToWorkoutSteps
        .from(workouts)
        .shouldMatchTo(Invalid(NoIntervalsInWorkout(workouts)))
    }

  }

  "Valid workout steps" in {

    forAll(sizeRange(3)) { steps: NonEmptyList[WorkoutStep] =>
      val workouts = toWorkoutData(steps)

      ToWorkoutSteps
        .from(workouts)
        .shouldMatchTo(Valid(steps))
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
      if (isLast) 0 to step.duration.toSeconds.toInt
      else 0 until step.duration.toSeconds.toInt

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
    offset: Time,
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
          offset = offset + Seconds(slice),
          memberFtpPercent = 0,
          ftpPercent = ftpRatio * 100,
        )
      }
  }

  def toWorkoutData(
    steps: NonEmptyList[WorkoutStep],
    offset: Time,
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
          offset = offset + Seconds(slice),
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
      offset = Seconds(0),
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
