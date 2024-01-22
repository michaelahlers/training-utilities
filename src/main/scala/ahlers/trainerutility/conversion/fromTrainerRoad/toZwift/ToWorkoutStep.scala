package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import Error.NoWorkoutsForInterval
import cats.data.Validated
import cats.syntax.validated._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState

private[fromTrainerRoad] object ToWorkoutStep {

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

        val ftpPowerLowPercent = workouts.map(_.ftpPercent).min
        val ftpPowerHighPercent = workouts.map(_.ftpPercent).max

        val ftpPowerLowRatio = ftpPowerLowPercent.round / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent.round / 100f

        val step: WorkoutStep =
          if (ftpPowerLowPercent == ftpPowerHighPercent) {
            SteadyState(
              durationSeconds = durationSeconds,
              ftpPowerRatio = ftpPowerLowRatio,
            )
          } else {

            Ramp(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerLowRatio,
              ftpPowerHighRatio = ftpPowerHighRatio,
            )
          }

        (step, next).valid
    }

  }

}
