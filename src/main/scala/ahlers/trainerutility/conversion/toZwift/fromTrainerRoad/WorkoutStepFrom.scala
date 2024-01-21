package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad

import ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.Error.NoWorkoutsForInterval
import cats.data.Validated
import cats.syntax.validated._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState

private[fromTrainerRoad] object WorkoutStepFrom {

  def apply(
    interval: IntervalData,
    workouts: Seq[WorkoutData],
  ): Validated[Error, (WorkoutStep, Seq[WorkoutData])] = {
    val (current, next) = workouts
      .partition { workout =>
        workout.centiseconds <
          interval.end * 100
      }

    current match {

      case Seq() =>
        NoWorkoutsForInterval(
          interval = interval,
        ).invalid

      case workouts =>
        val durationSeconds =
          interval.end -
            interval.start

        val ftpPowerLowPercent = workouts.map(_.ftpPercent).min
        val ftpPowerHighPercent = workouts.map(_.ftpPercent).max

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
