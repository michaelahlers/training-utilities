package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import Error.NoWorkoutsForInterval
import cats.data.Validated
import cats.data.Validated.Valid
import cats.syntax.validated._
import scala.annotation.tailrec
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

/*private[toZwift]*/
object ToWorkoutStep {

  sealed trait Slope
  object Slope {
    case object Undefined extends Slope
    case object Up extends Slope
    case object Flat extends Slope
    case object Down extends Slope

    def from(start: WorkoutData, end: WorkoutData): Slope =
      if (start.ftpPercent == end.ftpPercent) Flat
      else if (start.ftpPercent < end.ftpPercent) Up
      else Down

    def from(workouts: Seq[WorkoutData]): Slope =
      if (workouts.size < 2) Undefined
      else from(workouts.head, workouts.last)

  }

  sealed trait Phase
  object Phase {
    object First extends Phase
    object Interior extends Phase
    object Last extends Phase
  }

  def apply(
    workouts: Seq[WorkoutData],
  ): Validated[Error, (WorkoutStep, Seq[WorkoutData])] = {

    @tailrec
    def take(queue: List[WorkoutData], acc: Vector[WorkoutData]): ((WorkoutData, WorkoutData), Seq[WorkoutData]) = {
      val slope = Slope.from(acc)

      queue match {

        case _ :: Nil =>
          ((acc.head, acc.last), queue)

        case head :: tail if slope == Slope.Undefined =>
          take(
            queue = tail,
            acc = acc :+ head,
          )

        case head :: tail if slope == Slope.from(acc.last, head) =>
          take(
            queue = tail,
            acc = acc :+ head,
          )

        case head :: _ if slope != Slope.from(acc.last, head) =>
          val nextSlope = Slope.from(acc.last, head)
          pprint.log((slope, nextSlope))
          ((acc.head, acc.last), queue)

        case head :: _ if acc.last.ftpPercent != head.ftpPercent =>
          ((acc.head, acc.last), queue)

        case head :: tail =>
          take(
            queue = tail,
            acc = acc :+ head,
          )

      }
    }

    @tailrec
    def take2(queue: List[WorkoutData], acc: Vector[WorkoutData]): ((WorkoutData, WorkoutData), Seq[WorkoutData]) = {
      val slope = Slope.from(acc)

      queue match {

        /** Stop when the terminator is reached. */
        case head :: Nil =>
          ((acc.head, acc.last.copy(ftpPercent = head.ftpPercent)), queue)

        case head :: tail if slope == Slope.Undefined =>
          take2(
            queue = tail,
            acc = acc :+ head,
          )

        /**
         * Steady-state, with special case where the next interval begins with an inflection point but shares the same [[WorkoutData.ftpPercent]].
         */
        case head :: next :: tail if acc.last.ftpPercent == head.ftpPercent && head.ftpPercent == next.ftpPercent =>
          take2(
            queue = tail,
            acc = acc :+ head,
          )

        /** Continuous ramp. */
        case head :: next :: tail if slope == Slope.from(acc.last, head) && slope == Slope.from(head, next) =>
          take2(
            queue = next :: tail,
            acc = acc :+ head,
          )

        case _ =>
          ((acc.head, acc.last), queue)

      }
    }

    val ((start, end), next) = take2(
      queue = workouts.toList,
      acc = Vector.empty,
    )

    /** [[WorkoutFile.workout]] is order dependent, and only the duration is required. */
    val durationSeconds = (next.head.milliseconds - start.milliseconds) / 1000

    val phase: Phase =
      if (start.milliseconds == 0) Phase.First
      else if (next.size == 1) Phase.Last
      else Phase.Interior

    val slope: Slope = Slope.from(start, end)

    val step: WorkoutStep = (phase, slope) match {

      case (Phase.First, Slope.Up | Slope.Down) =>
        val ftpPowerLowPercent = start.ftpPercent
        val ftpPowerHighPercent = end.ftpPercent

        val ftpPowerLowRatio = ftpPowerLowPercent / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpPowerLowRatio = ftpPowerLowRatio,
          ftpPowerHighRatio = ftpPowerHighRatio,
        )

      case (Phase.First, Slope.Flat) =>
        val ftpPowerPercent = start.ftpPercent
        val ftpPowerRatio = ftpPowerPercent / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpPowerLowRatio = ftpPowerRatio,
          ftpPowerHighRatio = ftpPowerRatio,
        )

      case (Phase.Interior, Slope.Up | Slope.Down) =>
        val ftpPowerLowPercent = start.ftpPercent
        val ftpPowerHighPercent = end.ftpPercent

        val ftpPowerLowRatio = ftpPowerLowPercent / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent / 100f

        Ramp(
          durationSeconds = durationSeconds,
          ftpPowerLowRatio = ftpPowerLowRatio,
          ftpPowerHighRatio = ftpPowerHighRatio,
        )

      case (Phase.Interior, Slope.Flat) =>
        val ftpPowerPercent = start.ftpPercent
        val ftpPowerRatio = ftpPowerPercent / 100f

        SteadyState(
          durationSeconds = durationSeconds,
          ftpPowerRatio = ftpPowerRatio,
        )

      case (Phase.Last, Slope.Up | Slope.Down) =>
        val ftpPowerLowPercent = start.ftpPercent
        val ftpPowerHighPercent = end.ftpPercent

        val ftpPowerLowRatio = ftpPowerLowPercent / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpPowerLowRatio = ftpPowerLowRatio,
          ftpPowerHighRatio = ftpPowerHighRatio,
        )

      case (Phase.Last, Slope.Flat) =>
        val ftpPowerPercent = start.ftpPercent
        val ftpPowerRatio = ftpPowerPercent / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpPowerLowRatio = ftpPowerRatio,
          ftpPowerHighRatio = ftpPowerRatio,
        )

    }

    if (next.size == 1) (step, Nil).valid
    else (step, next).valid
  }

}
