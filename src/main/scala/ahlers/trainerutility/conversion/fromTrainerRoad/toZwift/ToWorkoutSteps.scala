package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.syntax.validated._
import scala.annotation.tailrec
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

  def foo(
    workouts: Seq[WorkoutData],
  ): Validated[Error, Seq[WorkoutStep]] = {

    @tailrec
    def take(
      workouts: List[WorkoutData],
      steps: Vector[WorkoutStep],
    ): Seq[WorkoutStep] = workouts match {
      case Nil => steps
      case workouts =>
        val (step, next) = ToWorkoutStep.foo(workouts).getOrElse(???)
        take(
          workouts = next.toList,
          steps = steps :+ step,
        )
    }

    take(
      workouts = workouts.toList,
      steps = Vector.empty,
    ).valid
  }

}
