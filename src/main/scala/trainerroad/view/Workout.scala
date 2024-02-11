package trainerroad.view

import cats.data.NonEmptyList
import trainerroad.schema.web.WorkoutData

sealed trait Workout
object Workout {

  sealed trait Slope
  object Slope {

    case object Undefined extends Slope

    sealed trait Defined {
      def ratio: Float
    }

    case object Zero extends Slope with Defined {
      override val ratio: Float = 0
    }

    case class Positive(ratio: Float) extends Slope with Defined

    case class Negative(ratio: Float) extends Slope with Defined

    def apply(value: Float): Slope with Defined =
      if (value > 0) Positive(value)
      else if (value < 0) Negative(value)
      else Zero

    def from(start: WorkoutData, end: WorkoutData): Slope with Defined =
      Slope {
        (end.ftpPercent - start.ftpPercent) /
          (end.milliseconds - start.milliseconds)
      }

    def from(workouts: Seq[WorkoutData]): Slope =
      if (workouts.size > 1) from(workouts.head, workouts.last)
      else Undefined

  }

  case class Nil(
    end: WorkoutData,
  ) extends Workout

  case class Cons(
    interval: NonEmptyList[WorkoutData],
    tail: Workout,
  ) extends Workout

}
