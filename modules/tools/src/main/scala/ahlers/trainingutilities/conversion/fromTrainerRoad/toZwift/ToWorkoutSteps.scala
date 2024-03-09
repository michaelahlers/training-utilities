package ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift

import cats.data.NonEmptyList
import cats.data.Validated
import cats.syntax.validated._
import scala.annotation.tailrec
import squants.time.Time
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
    stepList: StepList.Cons,
  ): WorkoutStep = {
    val duration: Time = stepList.duration
    val phase: Phase   = stepList.phase
    val slope: Slope = stepList match {
      case _: StepList.Inflection   => Slope.Undefined
      case stepList: StepList.Range => stepList.slope
    }

    val ftpPercentStart = stepList.start.ftpPercent
    val ftpPercentEnd = stepList match {
      case step: StepList.Inflection => step.start.ftpPercent
      case step: StepList.Range      =>
        /** The "terminating" [[WorkoutData.ftpPercent]] is included with the final interval. */
        phase match {
          case Phase.First | Phase.Interior => step.end.ftpPercent
          case Phase.Last                   => step.tail.start.ftpPercent
        }
    }

    val step: WorkoutStep = (phase, slope) match {

      case (Phase.First, _) =>
        Warmup(
          duration = duration,
          ftpPercentStart = ftpPercentStart,
          ftpPercentEnd = ftpPercentEnd,
        )

      case (Phase.Interior, Slope.Positive(_) | Slope.Negative(_)) =>
        Ramp(
          duration = duration,
          ftpPercentStart = ftpPercentStart,
          ftpPercentEnd = ftpPercentEnd,
        )

      case (Phase.Interior, Slope.Undefined | Slope.Zero) =>
        SteadyState(
          duration = duration,
          ftpPercent = ftpPercentStart,
        )

      case (Phase.Last, _) =>
        Cooldown(
          duration = duration,
          ftpPercentStart = ftpPercentStart,
          ftpPercentEnd = ftpPercentEnd,
        )

    }

    step
  }

  def from(
    workouts: NonEmptyList[WorkoutData],
  ): Validated[Throwable, NonEmptyList[WorkoutStep]] = {

    @tailrec
    def loop(
      queue: StepList,
      acc: Vector[WorkoutStep],
    ): Validated[Throwable, NonEmptyList[WorkoutStep]] = queue match {

      case head: StepList.End =>
        NonEmptyList
          .fromFoldable(acc)
          .map(_.valid)
          .getOrElse(NoIntervalsInWorkoutException(workouts).invalid)

      case head: StepList.Cons =>
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
