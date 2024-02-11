package trainerroad.schema.web

import cats.data.NonEmptyList
import io.circe.Decoder

case class Workout(
  details: Details,
  workoutData: NonEmptyList[WorkoutData],
  intervalData: NonEmptyList[IntervalData],
)

object Workout {

  implicit val decoder: Decoder[Workout] = cursor =>
    for {
      details <- cursor.downField("Details").as[Details]
      workoutData <- cursor.downField("workoutData").as[NonEmptyList[WorkoutData]]
      intervalData <- cursor.downField("intervalData").as[NonEmptyList[IntervalData]]
    } yield Workout(
      details = details,
      workoutData = workoutData,
      intervalData = intervalData,
    )
}
