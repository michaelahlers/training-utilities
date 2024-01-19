package trainerroad.schema.web

import io.circe.Decoder

case class Workout(
  details: Details,
  workoutData: Seq[WorkoutData],
  intervalData: Seq[IntervalData],
)

object Workout {

  implicit val decoder: Decoder[Workout] = cursor =>
    for {
      details <- cursor.downField("Details").as[Details]
      workoutData <- cursor.downField("workoutData").as[Seq[WorkoutData]]
      intervalData <- cursor.downField("intervalData").as[Seq[IntervalData]]
    } yield Workout(
      details = details,
      workoutData = workoutData,
      intervalData = intervalData,
    )
}
