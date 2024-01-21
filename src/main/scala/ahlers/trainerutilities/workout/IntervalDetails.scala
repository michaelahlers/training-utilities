package ahlers.trainerutilities.workout

import ahlers.trainerutilities.workout.Error.NoDetailsForInterval
import cats.data.NonEmptyChain
import cats.data.Validated
import cats.syntax.validated._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData

private[workout] case class IntervalDetails(
  interval: IntervalData,
  details: NonEmptyChain[WorkoutData],
) {
  import interval.end
  import interval.start

  require(
    details.forall { detail =>
      detail.centiseconds >= interval.start * 100 &&
      detail.centiseconds < interval.end * 100
    },
    s"Workout details must be within interval's start ($start, inclusive) and end ($end, exclusive).",
  )

}

private[workout] object IntervalDetails {

  def fromSeq(
    interval: IntervalData,
    details: Seq[WorkoutData],
  ): Validated[Error, IntervalDetails] = NonEmptyChain.fromSeq(details
    .takeWhile(_.centiseconds <= interval.end * 100))
    .map(details =>
      IntervalDetails(
        interval = interval,
        details = details,
      ).valid,
    )
    .getOrElse(NoDetailsForInterval(interval).invalid)

}
