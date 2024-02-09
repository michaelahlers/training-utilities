package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.NoWorkoutsForInterval
import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.UndefinedSlope
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.syntax.validated._
import scala.annotation.tailrec
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

private[toZwift] object ToWorkoutStep {

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

  case class Selection(
    start: WorkoutData,
    end: WorkoutData,
    remainder: Seq[WorkoutData],
  )

  @tailrec
  def select(
    queue: List[WorkoutData],
    acc: Vector[WorkoutData],
  ): Validated[Error, Selection] = {
    val slope = Slope.from(acc)

    (queue, slope) match {

      /** Meaningless when [[queue]] starts empty. */
      case (Nil, _) => NoWorkoutsForInterval.invalid

      /** Base case: stop when the terminator is reached. */
      case (head :: Nil, _) =>
        Selection(acc.head, acc.last.copy(ftpPercent = head.ftpPercent), queue).valid

      case (head :: tail, Invalid(_)) =>
        select(
          queue = tail,
          acc = acc :+ head,
        )

      /**
       * Steady-state, with special case where the next interval begins with an inflection point but shares the same [[WorkoutData.ftpPercent]].
       */
      case (head :: next :: tail, _) if acc.last.ftpPercent == head.ftpPercent && head.ftpPercent == next.ftpPercent =>
        select(
          queue = tail,
          acc = acc :+ head,
        )

      /** Continuous ramp. */
      case (head :: next :: tail, Valid(slope)) if slope == Slope.from(acc.last, head) && slope == Slope.from(head, next) =>
        select(
          queue = next :: tail,
          acc = acc :+ head,
        )

      case _ =>
        Selection(acc.head, acc.last, queue).valid

    }
  }

  def apply(
    workouts: Seq[WorkoutData],
  ): Validated[Error, (WorkoutStep, Seq[WorkoutData])] = for {
    selection <- select(
      queue = workouts.toList,
      acc = Vector.empty,
    )
  } yield {
    import selection.remainder
    import selection.start
    import selection.end

    /** [[WorkoutFile.workout]] is order dependent, and only the duration is required. */
    val durationSeconds = (remainder.head.milliseconds - start.milliseconds) / 1000

    val phase: Phase =
      if (start.milliseconds == 0) Phase.First
      else if (remainder.size == 1) Phase.Last
      else Phase.Interior

    val slope: Slope = Slope.from(start, end)

    val step: WorkoutStep = (phase, slope) match {

      case (Phase.First, Slope.Positive | Slope.Negative) =>
        val ftpPercentStart = start.ftpPercent
        val ftpPercentEnd = end.ftpPercent

        val ftpRatioStart = ftpPercentStart / 100f
        val ftpRatioEnd = ftpPercentEnd / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

      case (Phase.First, Slope.Zero) =>
        val ftpPercent = start.ftpPercent
        val ftpRatio = ftpPercent / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatio,
          ftpRatioEnd = ftpRatio,
        )

      case (Phase.Interior, Slope.Positive | Slope.Negative) =>
        val ftpPercentStart = start.ftpPercent
        val ftpPercentEnd = end.ftpPercent

        val ftpRatioStart = ftpPercentStart / 100f
        val ftpRatioEnd = ftpPercentEnd / 100f

        Ramp(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

      case (Phase.Interior, Slope.Zero) =>
        val ftpPercent = start.ftpPercent
        val ftpRatio = ftpPercent / 100f

        SteadyState(
          durationSeconds = durationSeconds,
          ftpRatio = ftpRatio,
        )

      case (Phase.Last, Slope.Positive | Slope.Negative) =>
        val ftpPercentStart = start.ftpPercent
        val ftpPercentEnd = end.ftpPercent

        val ftpRatioStart = ftpPercentStart / 100f
        val ftpRatioEnd = ftpPercentEnd / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

      case (Phase.Last, Slope.Zero) =>
        val ftpPercent = start.ftpPercent
        val ftpRatio = ftpPercent / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatio,
          ftpRatioEnd = ftpRatio,
        )

    }

    if (remainder.size == 1) (step, Nil)
    else (step, remainder)
  }

}
