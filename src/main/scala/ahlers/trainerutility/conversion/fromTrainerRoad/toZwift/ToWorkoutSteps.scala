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
    workouts: Seq[WorkoutData],
  ): Validated[Error, Seq[WorkoutStep]] = {

    @tailrec
    def take(
      workouts: List[WorkoutData],
      steps: Vector[WorkoutStep],
    ): Seq[WorkoutStep] = workouts match {
      case Nil => steps
      case workouts =>
        val (step, next) = ToWorkoutStep.from(workouts).getOrElse(???)
        take(
          workouts = next.toList,
          steps = steps :+ step,
        )
    }

    val steps = take(
      workouts = workouts.toList,
      steps = Vector.empty,
    )

    steps.valid
  }

}
