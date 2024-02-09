package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import Error.NoWorkoutsForInterval
import Error.UndefinedSlope
import cats.data.Validated
import cats.data.Validated.Invalid
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
    case object Positive extends Slope
    case object Zero extends Slope
    case object Negative extends Slope

    def from(start: WorkoutData, end: WorkoutData): Slope =
      if (start.ftpPercent == end.ftpPercent) Zero
      else if (start.ftpPercent < end.ftpPercent) Positive
      else Negative

    def from(workouts: Seq[WorkoutData]): Validated[Error, Slope] =
      if (workouts.size > 1) from(workouts.head, workouts.last).valid
      else UndefinedSlope(workouts).invalid
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
    def take2(
      queue: List[WorkoutData],
      acc: Vector[WorkoutData],
    ): ((WorkoutData, WorkoutData), Seq[WorkoutData]) = {
      val slope = Slope.from(acc)

      (queue, slope) match {

        /** Stop when the terminator is reached. */
        case (head :: Nil, _) =>
          ((acc.head, acc.last.copy(ftpPercent = head.ftpPercent)), queue)

        case (head :: tail, Invalid(_)) =>
          take2(
            queue = tail,
            acc = acc :+ head,
          )

        /**
         * Steady-state, with special case where the next interval begins with an inflection point but shares the same [[WorkoutData.ftpPercent]].
         */
        case (head :: next :: tail, _) if acc.last.ftpPercent == head.ftpPercent && head.ftpPercent == next.ftpPercent =>
          take2(
            queue = tail,
            acc = acc :+ head,
          )

        /** Continuous ramp. */
        case (head :: next :: tail, Valid(slope)) if slope == Slope.from(acc.last, head) && slope == Slope.from(head, next) =>
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

      case (Phase.First, Slope.Positive | Slope.Negative) =>
        val ftpPowerLowPercent = start.ftpPercent
        val ftpPowerHighPercent = end.ftpPercent

        val ftpPowerLowRatio = ftpPowerLowPercent / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpPowerLowRatio,
          ftpRatioEnd = ftpPowerHighRatio,
        )

      case (Phase.First, Slope.Zero) =>
        val ftpPowerPercent = start.ftpPercent
        val ftpPowerRatio = ftpPowerPercent / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpPowerRatio,
          ftpRatioEnd = ftpPowerRatio,
        )

      case (Phase.Interior, Slope.Positive | Slope.Negative) =>
        val ftpPowerLowPercent = start.ftpPercent
        val ftpPowerHighPercent = end.ftpPercent

        val ftpPowerLowRatio = ftpPowerLowPercent / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent / 100f

        Ramp(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpPowerLowRatio,
          ftpRatioEnd = ftpPowerHighRatio,
        )

      case (Phase.Interior, Slope.Zero) =>
        val ftpPowerPercent = start.ftpPercent
        val ftpPowerRatio = ftpPowerPercent / 100f

        SteadyState(
          durationSeconds = durationSeconds,
          ftpRatio = ftpPowerRatio,
        )

      case (Phase.Last, Slope.Positive | Slope.Negative) =>
        val ftpPowerLowPercent = start.ftpPercent
        val ftpPowerHighPercent = end.ftpPercent

        val ftpPowerLowRatio = ftpPowerLowPercent / 100f
        val ftpPowerHighRatio = ftpPowerHighPercent / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpPowerLowRatio,
          ftpRatioEnd = ftpPowerHighRatio,
        )

      case (Phase.Last, Slope.Zero) =>
        val ftpPowerPercent = start.ftpPercent
        val ftpPowerRatio = ftpPowerPercent / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpPowerRatio,
          ftpRatioEnd = ftpPowerRatio,
        )

    }

    if (next.size == 1) (step, Nil).valid
    else (step, next).valid
  }

}
