package trainerroad.schema.web

import cats.data.NonEmptyList
import cats.data.zio.json.instances._
import io.circe.Decoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class Workout(
  @jsonField("Details") details: Details,
  workoutData: NonEmptyList[WorkoutData],
  intervalData: NonEmptyList[IntervalData],
)

object Workout {

  implicit val circeDecoder: Decoder[Workout] = cursor =>
    for {
      details      <- cursor.downField("Details").as[Details]
      workoutData  <- cursor.downField("workoutData").as[NonEmptyList[WorkoutData]]
      intervalData <- cursor.downField("intervalData").as[NonEmptyList[IntervalData]]
    } yield Workout(
      details = details,
      workoutData = workoutData,
      intervalData = intervalData,
    )

  implicit val zioDecoder: JsonDecoder[Workout] =
    DeriveJsonDecoder.gen[Workout]

}
