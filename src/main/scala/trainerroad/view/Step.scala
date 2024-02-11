package trainerroad.view

import cats.Now
import cats.data.NonEmptyList
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import scala.annotation.tailrec
import trainerroad.schema.web.WorkoutData

sealed trait Step {
  def start: WorkoutData
}

object Step {

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
    end: WorkoutData,
  ) extends Step {
    override val start: WorkoutData = end
  }

  case class Head(
    interval: NonEmptyList[WorkoutData],
    tail: Step,
  ) extends Step {
    override val start: WorkoutData = interval.head
    val slope = Slope.from(start, tail.start)
  }

  object Head {
    def apply(
      interval: WorkoutData,
      tail: Step,
    ): Head = Head(
      interval = NonEmptyList.one(interval),
      tail = tail,
    )
  }

  def from(
    workouts: NonEmptyList[WorkoutData],
  ): Step = {

    @tailrec
    def loop(
      queue: List[WorkoutData],
      acc: Step,
    ): Step =
      (queue, acc) match {

        case (Nil, acc) => acc

        case (head :: tail, acc: Empty) =>
          loop(
            queue = tail,
            acc = Head(
              interval = head,
              tail = acc,
            ),
          )

        case (head :: tail, acc: Head) if acc.start.ftpPercent === head.ftpPercent =>
          loop(
            queue = tail,
            acc = Head(
              interval = head :: acc.interval,
              tail = acc.tail,
            ),
          )

        case (head :: tail, acc: Head) if acc.slope.ratio === Slope.from(acc.start, head).ratio +- 0.0001f =>
          loop(
            queue = tail,
            acc = Head(
              interval = head :: acc.interval,
              tail = acc.tail,
            ),
          )

      }

    loop(
      queue = workouts.reverse.init,
      acc = Empty(workouts.last),
    )
  }

}
