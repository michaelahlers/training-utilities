package trainerroad.view

import cats.data.NonEmptyList
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import squants.time.Time
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData

sealed trait StepList {
  def start: WorkoutData
}

object StepList {

  sealed trait Phase
  object Phase {
    object First    extends Phase
    object Interior extends Phase
    object Last     extends Phase
  }

  sealed trait Slope
  object Slope {

    /** No slope exists for an instantaneous point (''i.e.'', an [[Inflection]]). */
    case object Undefined extends Slope

    /** Slope ''is'' defined (over a [[Range]]). */
    sealed trait Defined

    /** [[Range.start]] and [[Range.end]] have identical [[WorkoutData.ftpPercent]]. */
    case object Zero extends Slope with Defined

    /** [[Range.start]] and [[Range.end]] are not the same [[WorkoutData.ftpPercent]]. */
    sealed trait NonZero {
      def ratio: Float
    }

    object NonZero {
      def unapply(slope: NonZero): Option[Float] =
        slope match {
          case slope: NonZero => Some(slope.ratio)
          case _              => None
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

    /**
     * The [[Slope]] between two [[WorkoutData]] with different [[WorkoutData.offset]] is inherently [[Defined]].
     * @todo Return [[cats.data.Validated.Invalid]] when the [[WorkoutData.offset]] values are the same.
     */
    def from(start: WorkoutData, end: WorkoutData): Slope with Slope.Defined =
      Slope {
        (end.ftpPercent - start.ftpPercent) /
          (end.offset - start.offset).millis
      }

  }

  /**
   * Given [[WorkoutData.offset]] is zero-indexed, [[Workout.workoutData.last]] is a special case that should start a new interval, but does inform the last interval of a workout, including it's [[Phase]], [[Cons.duration]], and so on.
   */
  case class End(
    start: WorkoutData,
  ) extends StepList

  /**
   * Represents an internal element of a [[StepList]].
   */
  sealed trait Cons {
    def start: WorkoutData
    def tail: StepList

    final val duration: Time = tail.start.offset - start.offset

    final val phase: Phase = {
      val isFirst = start.offset.millis == 0

      val isLast = tail match {
        case _: StepList.End => true
        case _               => false
      }

      /** Accommodates a special case where the given [[StepList.Cons]] is alone. */
      if (isFirst && isLast) Phase.Interior
      else if (isFirst) Phase.First
      else if (isLast) Phase.Last
      else Phase.Interior
    }
  }

  case class Inflection(
    start: WorkoutData,
    tail: StepList,
  ) extends StepList with Cons

  sealed trait Range {
    def start: WorkoutData
    def end: WorkoutData

    final val slope: Slope with Slope.Defined = Slope.from(start, end)
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
  ) extends StepList with Cons with Range

  case class Ramp(
    start: WorkoutData,
    end: WorkoutData,
    tail: StepList,
  ) extends StepList with Cons with Range

  def apply(
    start: WorkoutData,
  ): StepList = End(start)

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

    /**
     * @return `true` ''iff'' the [[WorkoutData.ftpPercent]] of `last` and `next` are the same.
     */
    def isSamePower(
      last: Flat,
      next: WorkoutData,
    ): Boolean = {
      val lastFtpPercent = last.start.ftpPercent
      val nextFtpPercent = next.ftpPercent

      lastFtpPercent === nextFtpPercent
    }

    /**
     * @return `true` ''iff'' the [[Slope]] of `last` and `next` are either both [[Slope.Zero]] ''or'' their [[Slope.NonZero.ratio]] are within a small tolerance.
     * @todo Make tolerance configurable.
     */
    def isSameSlope(
      last: Ramp,
      next: WorkoutData,
    ): Boolean = {
      val lastSlope = last.slope
      val nextSlope = Slope.from(last.start, next)

      (lastSlope, nextSlope) match {
        case (Slope.Zero, Slope.Zero)                             => true
        case (Slope.NonZero(lastRatio), Slope.NonZero(nextRatio)) => lastRatio === nextRatio +- 0.001f
        case _                                                    => false
      }
    }

    workouts.init.foldRight(StepList(workouts.last)) {

      case (head, acc: End) =>
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
