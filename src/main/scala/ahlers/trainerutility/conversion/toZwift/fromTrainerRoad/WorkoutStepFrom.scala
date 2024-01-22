package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad

import ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.Error.NoWorkoutsForInterval
import cats.data.Validated
import cats.syntax.validated._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState

private[fromTrainerRoad] object WorkoutStepFrom {

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
        val durationSeconds = interval.end - interval.start
        val ftpPowerLowPercent = workouts.map(_.ftpPercent).min.round
        val ftpPowerHighPercent = workouts.map(_.ftpPercent).max.round

        val step: WorkoutStep =
          if (ftpPowerLowPercent == ftpPowerHighPercent) {
            SteadyState(
              durationSeconds = durationSeconds,
              ftpPowerRatio = ftpPowerLowPercent / 100f,
            )
          } else {
            Ramp(
              durationSeconds = durationSeconds,
              ftpPowerLowRatio = ftpPowerLowPercent / 100f,
              ftpPowerHighRatio = ftpPowerHighPercent / 100f,
            )
          }

        (step, next).valid
    }

  }

}
