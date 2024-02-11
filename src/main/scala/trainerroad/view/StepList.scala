package trainerroad.view

import cats.data.NonEmptyList
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import scala.annotation.tailrec
import squants.time.Milliseconds
import squants.time.Time
import trainerroad.schema.web.WorkoutData
import trainerroad.view.StepList.Empty
import trainerroad.view.StepList.Instant
import trainerroad.view.StepList.Range

sealed trait StepList { self =>
  def start: WorkoutData

  /** @todo Remove temporary debugging support (implement as [[cats.Foldable]]). */
  @tailrec
  final def foreach[A](f: StepList => A): Unit =
    self match {
      case step: Empty => f(step)
      case step: Instant =>
        f(step)
        step.tail.foreach(f)
      case step: Range =>
        f(step)
        step.tail.foreach(f)
    }

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

//case class Head(
//  interval: NonEmptyList[WorkoutData],
//  tail: StepList,
//) extends StepList {
//  override val start: WorkoutData = interval.head
//  val slope: Slope with Slope.Defined = Slope.from(start, tail.start)
//  val duration: Time = Milliseconds(tail.start.milliseconds - start.milliseconds)
//}

//object Head {
//  def apply(
//    interval: WorkoutData,
//    tail: StepList,
//  ): Head = Head(
//    interval = NonEmptyList.one(interval),
//    tail = tail,
//  )
//}

  def from(
    workouts: NonEmptyList[WorkoutData],
  ): StepList = {

    @tailrec
    def loop(
      queue: List[WorkoutData],
      acc: StepList,
    ): StepList = {

      def isContinuous(
        step: Range,
        next: WorkoutData,
      ): Boolean =
        step.start.ftpPercent === next.ftpPercent ||
          step.slope.ratio === Slope.from(step.start, next).ratio +- 0.0001f

      (queue, acc) match {

        case (Nil, acc) => acc

        case (head :: tail, acc: Empty) =>
          loop(
            queue = tail,
            acc = Instant(
              start = head,
              tail = acc,
            ),
          )

        case (head :: tail, acc: Instant) =>
          loop(
            queue = tail,
            acc = Range(
              start = head,
              end = acc.start,
              tail = acc.tail,
            ),
          )

        case (head :: tail, acc: Range) if isContinuous(acc, head) =>
          loop(
            queue = tail,
            acc = acc.copy(
              start = head,
            ),
          )

        /** Inflection point. */
        case (head :: tail, acc: Range) =>
          loop(
            queue = tail,
            acc = Instant(
              start = head,
              tail = acc,
            ),
          )

      }
    }

    loop(
      queue = workouts.init.reverse,
      acc = Empty(workouts.last),
    )
  }

}
