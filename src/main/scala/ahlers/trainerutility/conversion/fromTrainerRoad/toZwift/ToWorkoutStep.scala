package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import Error.NoWorkoutsForInterval
import cats.data.Validated
import cats.syntax.validated._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

private[toZwift] object ToWorkoutStep {

  sealed trait Slope
  object Slope {
    case object Up extends Slope
    case object Flat extends Slope
    case object Down extends Slope
  }

  sealed trait Phase
  object Phase {
    object First extends Phase
    object Interior extends Phase
    object Last extends Phase
  }

  def apply(
    interval: IntervalData,
    workouts: Seq[WorkoutData],
  ): Validated[Error, (WorkoutStep, Seq[WorkoutData])] = {

    /**
     * [[current]] contains all [[WorkoutData]] values within the given [[interval]] range.
     * [[next]] are all those remaining, which is non-empty since [[Workout.workoutData]], even being zero-indexed, always contains a terminator.
     */
    val (current: Seq[WorkoutData], next: Seq[WorkoutData]) = workouts
      .partition { workout =>
        workout.milliseconds <
          interval.end * 1000
      }

    current match {

      case Seq() =>
        NoWorkoutsForInterval(
          interval = interval,
        ).invalid

      case workouts =>
        /** [[WorkoutFile.workout]] is order dependent, and only the duration is required. */
        val durationSeconds = interval.end - interval.start

        val phase: Phase =
          if (interval.start == 0) Phase.First
          else if (next.size == 1) Phase.Last
          else Phase.Interior

        val slope: Slope =
          if (workouts.head.ftpPercent == workouts.last.ftpPercent) Slope.Flat
          else if (workouts.head.ftpPercent < workouts.last.ftpPercent) Slope.Up
          else Slope.Down

        val step: WorkoutStep = (phase, slope) match {

          case (Phase.First, Slope.Up | Slope.Down) =>
            val ftpPowerLowPercent = workouts.head.ftpPercent.min(workouts.last.ftpPercent)
            val ftpPowerHighPercent = workouts.last.ftpPercent.max(workouts.head.ftpPercent)

            val ftpPowerLowRatio = ftpPowerLowPercent / 100f
            val ftpPowerHighRatio = ftpPowerHighPercent / 100f

            Warmup(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerLowRatio,
              ftpPowerHighRatio = ftpPowerHighRatio,
            )

          case (Phase.First, Slope.Flat) =>
            val ftpPowerPercent = workouts.head.ftpPercent
            val ftpPowerRatio = ftpPowerPercent / 100f

            Warmup(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerRatio,
              ftpPowerHighRatio = ftpPowerRatio,
            )

          case (Phase.Interior, Slope.Up | Slope.Down) =>
            val ftpPowerLowPercent = workouts.head.ftpPercent.min(workouts.last.ftpPercent)
            val ftpPowerHighPercent = workouts.last.ftpPercent.max(workouts.head.ftpPercent)

            val ftpPowerLowRatio = ftpPowerLowPercent / 100f
            val ftpPowerHighRatio = ftpPowerHighPercent / 100f

            Ramp(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerLowRatio,
              ftpPowerHighRatio = ftpPowerHighRatio,
            )

          case (Phase.Interior, Slope.Flat) =>
            val ftpPowerPercent = workouts.head.ftpPercent
            val ftpPowerRatio = ftpPowerPercent / 100f

            SteadyState(
              durationSeconds = durationSeconds,
              ftpPowerRatio = ftpPowerRatio,
            )

          case (Phase.Last, Slope.Up | Slope.Down) =>
            val ftpPowerLowPercent = workouts.head.ftpPercent.min(workouts.last.ftpPercent)
            val ftpPowerHighPercent = workouts.last.ftpPercent.max(workouts.head.ftpPercent)

            val ftpPowerLowRatio = ftpPowerLowPercent / 100f
            val ftpPowerHighRatio = ftpPowerHighPercent / 100f

            Cooldown(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerLowRatio,
              ftpPowerHighRatio = ftpPowerHighRatio,
            )

          case (Phase.Last, Slope.Flat) =>
            val ftpPowerPercent = workouts.head.ftpPercent
            val ftpPowerRatio = ftpPowerPercent / 100f

            Cooldown(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerRatio,
              ftpPowerHighRatio = ftpPowerRatio,
            )

        }

        (step, next).valid
    }

  }

}
