package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.Error.NoIntervalsInWorkout
import cats.data.NonEmptyList
import cats.data.Validated
import cats.syntax.validated._
import scala.annotation.tailrec
import trainerroad.schema.web.WorkoutData
import trainerroad.view.StepList
import trainerroad.view.StepList.Phase
import trainerroad.view.StepList.Slope
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

private[toZwift] object ToWorkoutSteps {

  def from(
    stepList: StepList.Head,
  ): WorkoutStep = {
    val durationSeconds: Int = stepList.duration.toSeconds.toInt

    val phase: Phase = stepList.phase
    val slope: Slope = stepList.slope

    val ftpPercentStart = stepList.start.ftpPercent
    val ftpPercentEnd = stepList match {
      case step: StepList.Instant => step.start.ftpPercent
      case step: StepList.Range =>
        /** The "terminating" [[WorkoutData.ftpPercent]] is included with the final interval. */
        phase match {
          case Phase.First | Phase.Interior => step.end.ftpPercent
          case Phase.Last => step.tail.start.ftpPercent
        }
    }

    val ftpRatioStart = ftpPercentStart / 100f
    val ftpRatioEnd = ftpPercentEnd / 100f

    val step: WorkoutStep = (phase, slope) match {

      case (Phase.First, _) =>
        Warmup(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

      case (Phase.Interior, Slope.Positive(_) | Slope.Negative(_)) =>
        Ramp(
          durationSeconds = durationSeconds,
          ftpRatioStart = ftpRatioStart,
          ftpRatioEnd = ftpRatioEnd,
        )

      case (Phase.Interior, Slope.Undefined | Slope.Zero) =>
        SteadyState(
          durationSeconds = durationSeconds,
          ftpRatio = ftpRatioStart,
        )

      case (Phase.Last, _) =>
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
    workouts: NonEmptyList[WorkoutData],
  ): Validated[Error, NonEmptyList[WorkoutStep]] = {

    @tailrec
    def loop(
      queue: StepList,
      acc: Vector[WorkoutStep],
    ): Validated[Error, NonEmptyList[WorkoutStep]] = queue match {

      case head: StepList.Empty =>
        NonEmptyList
          .fromFoldable(acc)
          .map(_.valid)
          .getOrElse(NoIntervalsInWorkout(workouts).invalid)

      case head: StepList.Head =>
        val step = from(head)

        loop(
          queue = head.tail,
          acc = acc :+ step,
        )

    }

    loop(
      queue = StepList.from(workouts),
      acc = Vector.empty,
    )
  }

}
