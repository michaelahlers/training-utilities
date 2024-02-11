package trainerroad.view

import cats.data.NonEmptyList
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import squants.time.Time
import trainerroad.schema.web.WorkoutData

sealed trait StepList {
  def start: WorkoutData
}

object StepList {

  sealed trait Phase
  object Phase {
    object First extends Phase
    object Interior extends Phase
    object Last extends Phase
  }

  sealed trait Slope
  object Slope {

    case object Undefined extends Slope

    sealed trait Defined

    case object Zero extends Slope with Defined

    sealed trait NonZero {
      def ratio: Float
    }

    object NonZero {
      def unapply(slope: NonZero): Option[Float] =
        slope match {
          case slope: NonZero => Some(slope.ratio)
          case _ => None
        }
    }

    case class Positive(
      ratio: Float,
    ) extends Slope with Defined with NonZero

    case class Negative(
      ratio: Float,
    ) extends Slope with Defined with NonZero

    def apply(value: Float): Slope with Defined =
      if (value > 0) Positive(value)
      else if (value < 0) Negative(value)
      else Zero

    def from(start: WorkoutData, end: WorkoutData): Slope with Defined =
      Slope {
        (end.ftpPercent - start.ftpPercent) /
          (end.offset - start.offset).millis
      }

  }

  sealed trait Head {
    def start: WorkoutData
    def tail: StepList
    def slope: Slope

    final val duration: Time = tail.start.offset - start.offset

    final val phase: Phase = {
      val isFirst = start.offset.millis == 0

      val isLast = tail match {
        case _: StepList.Empty => true
        case _ => false
      }

      /** Accommodates a special case where the given [[StepList.Head]] is alone. */
      if (isFirst && isLast) Phase.Interior
      else if (isFirst) Phase.First
      else if (isLast) Phase.Last
      else Phase.Interior
    }
  }

  case class Empty(
    start: WorkoutData,
  ) extends StepList

  case class Inflection(
    start: WorkoutData,
    tail: StepList,
  ) extends StepList with Head {
    final override val slope: Slope.Undefined.type = Slope.Undefined
  }

  sealed trait Range extends Head {
    def end: WorkoutData
    def tail: StepList

    final override val slope: Slope with Slope.Defined = Slope.from(start, end)
  }

  object Range {
    def apply(
      start: WorkoutData,
      end: WorkoutData,
      tail: StepList,
    ): StepList with Range =
      if (Slope.from(start, end) == Slope.Zero) Flat(start, end, tail)
      else Ramp(start, end, tail)
  }

  case class Flat(
    start: WorkoutData,
    end: WorkoutData,
    tail: StepList,
  ) extends StepList with Range

  case class Ramp(
    start: WorkoutData,
    end: WorkoutData,
    tail: StepList,
  ) extends StepList with Range

  def apply(
    start: WorkoutData,
  ): StepList = Empty(start)

  def apply(
    start: WorkoutData,
    tail: StepList,
  ): StepList = Inflection(start, tail)

  def apply(
    start: WorkoutData,
    end: WorkoutData,
    tail: StepList,
  ): StepList = Range(start, end, tail)

  def from(
    workouts: NonEmptyList[WorkoutData],
  ): StepList = {

    def isSamePower(
      last: Flat,
      next: WorkoutData,
    ): Boolean = {
      val lastFtpPercent = last.start.ftpPercent
      val nextFtpPercent = next.ftpPercent
      lastFtpPercent === nextFtpPercent
    }

    def isSameSlope(
      last: Ramp,
      next: WorkoutData,
    ): Boolean = {
      val lastSlope = last.slope
      val nextSlope = Slope.from(last.start, next)

      (lastSlope, nextSlope) match {
        case (Slope.Zero, Slope.Zero) => true
        case (Slope.NonZero(lastRatio), Slope.NonZero(nextRatio)) => lastRatio === nextRatio +- 0.0000001f
        case _ => false
      }
    }

    workouts.init.foldRight(StepList(workouts.last)) {

      case (head, acc: Empty) =>
        Inflection(
          start = head,
          tail = acc,
        )

      case (head, acc: Inflection) =>
        Range(
          start = head,
          end = acc.start,
          tail = acc.tail,
        )

      case (head, acc: Flat) if isSamePower(acc, head) =>
        acc.copy(
          start = head,
        )

      case (head, acc: Ramp) if isSameSlope(acc, head) =>
        acc.copy(
          start = head,
        )

      case (head, acc: Range) =>
        Inflection(
          start = head,
          tail = acc,
        )

    }
  }

}
