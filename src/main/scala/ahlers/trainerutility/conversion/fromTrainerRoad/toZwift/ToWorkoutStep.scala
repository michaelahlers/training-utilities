package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.NoWorkoutsForStep
import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.ToWorkoutStep.Slope.Defined
import cats.data.Validated
import cats.syntax.validated._
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import scala.annotation.tailrec
import trainerroad.schema.web.WorkoutData
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

object ToWorkoutStep {

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
      case (Nil, _) => NoWorkoutsForStep.invalid

      /** Base case: stop when the terminator is reached. */
      case (head :: Nil, _) =>
        Selection(acc.head, acc.last.copy(ftpPercent = head.ftpPercent), queue).valid

      case (head :: tail, Slope.Undefined) =>
        select(
          queue = tail,
          acc = acc :+ head,
        )

      case (head :: tail, Slope.Zero) if acc.last.ftpPercent == head.ftpPercent =>
        select(
          queue = tail,
          acc = acc :+ head,
        )

      case (head :: tail, slope: Defined) if slope.ratio === Slope.from(acc.last, head).ratio +- 0.001f =>
        select(
          queue = tail,
          acc = acc :+ head,
        )

      case _ =>
        Selection(acc.head, acc.last, queue).valid

    }
  }

  private def from(
    start: WorkoutData,
    end: WorkoutData,
    remainder: Seq[WorkoutData],
  ): WorkoutStep = {

    /** [[WorkoutFile.workout]] is order dependent, and only the duration is required. */
    val durationSeconds = (remainder.head.milliseconds - start.milliseconds) / 1000

    val phase: Phase =
      if (start.milliseconds == 0) Phase.First
      else if (remainder.size == 1) Phase.Last
      else Phase.Interior

    val slope: Slope = Slope.from(start, end)

    val step: WorkoutStep = (phase, slope) match {

      case (Phase.First, _) =>
        val ftpPercentStart = start.ftpPercent
        val ftpPercentEnd = end.ftpPercent

        val ftpRatioStart = ftpPercentStart / 100f
        val ftpRatioEnd = ftpPercentEnd / 100f

        Warmup(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

      case (Phase.Interior, Slope.Positive(_) | Slope.Negative(_)) =>
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

      case (Phase.Last, _) =>
        val ftpPercentStart = start.ftpPercent
        val ftpPercentEnd = end.ftpPercent

        val ftpRatioStart = ftpPercentStart / 100f
        val ftpRatioEnd = ftpPercentEnd / 100f

        Cooldown(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

    }

    step
  }

  def from(
    workouts: Seq[WorkoutData],
  ): Validated[Error, (WorkoutStep, Seq[WorkoutData])] = for {
    selection <- select(
      queue = workouts.toList,
      acc = Vector.empty,
    )
  } yield {
    import selection.{end, remainder, start}

    val step = from(
      start = start,
      end = end,
      remainder = remainder,
    )

    if (remainder.size == 1) (step, Nil)
    else (step, remainder)
  }

}
