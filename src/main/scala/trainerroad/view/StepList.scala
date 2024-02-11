package trainerroad.view

import cats.data.NonEmptyList
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import squants.time.Milliseconds
import squants.time.Time
import trainerroad.schema.web.WorkoutData

sealed trait StepList {
  def start: WorkoutData
}

object StepList {

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

  case class Empty(
    start: WorkoutData,
  ) extends StepList

  case class Instant(
    start: WorkoutData,
    tail: StepList,
  ) extends StepList {
    val duration: Time = Milliseconds(tail.start.milliseconds - start.milliseconds)
  }

  case class Range(
    start: WorkoutData,
    end: WorkoutData,
    tail: StepList,
  ) extends StepList {
    val slope: Slope with Slope.Defined = Slope.from(start, end)
    val duration: Time = Milliseconds(tail.start.milliseconds - start.milliseconds)
  }

  def apply(
    start: WorkoutData,
  ): StepList = Empty(start)

  def apply(
    start: WorkoutData,
    tail: StepList,
  ): StepList = Instant(start, tail)

  def apply(
    start: WorkoutData,
    end: WorkoutData,
    tail: StepList,
  ): StepList = Range(start, end, tail)

  def from(
    workouts: NonEmptyList[WorkoutData],
  ): StepList = {

    def isContinuous(
      step: Range,
      next: WorkoutData,
    ): Boolean =
      step.start.ftpPercent === next.ftpPercent ||
        step.slope.ratio === Slope.from(step.start, next).ratio +- 0.0001f

    workouts.init.foldRight(StepList(workouts.last)) {

      case (head, acc: Empty) =>
        Instant(
          start = head,
          tail = acc,
        )

      case (head, acc: Instant) =>
        Range(
          start = head,
          end = acc.start,
          tail = acc.tail,
        )

      case (head, acc: Range) if isContinuous(acc, head) =>
        acc.copy(
          start = head,
        )

      /** Inflection point. */
      case (head, acc: Range) =>
        Instant(
          start = head,
          tail = acc,
        )

    }
  }

}
