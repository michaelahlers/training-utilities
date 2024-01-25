package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.syntax.validated._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutStep

private[toZwift] object ToWorkoutSteps {

  def apply(
    interval: Seq[IntervalData],
    workouts: Seq[WorkoutData],
  ): Validated[Error, Seq[WorkoutStep]] =
    interval
      .filterNot { interval =>
        /** Special case [[IntervalData]] that covers the entire session but doesn't inform the steps. */
        interval.name == "Workout"
      }
      .foldLeft((Vector.empty[WorkoutStep], workouts).valid[Error]) {
        case (result @ Invalid(_), _) => result
        case (Valid((steps, workouts)), interval) =>
          ToWorkoutStep(
            interval = interval,
            workouts = workouts,
          ).map { case (step, workouts) =>
            (steps :+ step, workouts)
          }
      }
      .map { case (steps, _) =>
        steps
      }

}
