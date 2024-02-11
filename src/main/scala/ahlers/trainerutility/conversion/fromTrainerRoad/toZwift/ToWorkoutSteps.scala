package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.NoWorkoutsForStep
import cats.data.NonEmptyList
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.syntax.validated._
import org.scalactic.Tolerance._
import org.scalactic.TripleEquals._
import scala.annotation.tailrec
import trainerroad.schema.web.WorkoutData
import trainerroad.view.StepList.Slope
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

private[toZwift] object ToWorkoutSteps {

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

    def isContinuous(
      slope: Slope with Slope.Defined,
      last: WorkoutData,
      next: WorkoutData,
    ): Boolean =
      last.ftpPercent === next.ftpPercent ||
        slope.ratio === Slope.from(acc.last, next).ratio +- 0.0001f

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

      case (head :: tail, slope: Slope.Defined) if isContinuous(slope, acc.last, head) =>
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

      case (Phase.Interior, Slope.Undefined | Slope.Zero) =>
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

  private def from1(
    remainder: Seq[WorkoutData],
  ): Validated[Error, (WorkoutStep, Seq[WorkoutData])] = for {
    selection <- select(
      queue = remainder.toList,
      acc = Vector.empty,
    )
  } yield {
    val step = from(
      start = selection.start,
      end = selection.end,
      remainder = selection.remainder,
    )

    if (selection.remainder.size == 1) (step, Nil)
    else (step, selection.remainder)
  }

  def from(
    workouts: NonEmptyList[WorkoutData],
  ): Validated[Error, NonEmptyList[WorkoutStep]] = {

    @tailrec
    def take(
      remainder: Seq[WorkoutData],
      steps: Vector[WorkoutStep],
    ): Validated[Error, NonEmptyList[WorkoutStep]] =
      from1(remainder) match {
        case result @ Invalid(_) if steps.isEmpty => result
        /** @todo Handle empty case correctly. */
        case Invalid(_) => NonEmptyList.fromFoldable(steps).get.valid
        case Valid((step, remainder)) =>
          take(
            remainder = remainder,
            steps = steps :+ step,
          )
      }

    val steps = take(
      remainder = workouts.toList,
      steps = Vector.empty,
    )

    steps
  }

}
