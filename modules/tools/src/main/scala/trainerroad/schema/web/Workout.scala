package trainerroad.schema.web

import cats.data.NonEmptyList
import cats.data.zio.json.instances._
import io.circe.Decoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class Workout(
  @jsonField("Details") details: Details,
  @jsonField("Tags") tags: Seq[String],
  workoutData: NonEmptyList[WorkoutData],
  intervalData: NonEmptyList[IntervalData],
  @jsonField("ProfileName") profileName: String,
)

object Workout {

  implicit val circeDecoder: Decoder[Workout] = cursor =>
    for {
      details      <- cursor.downField("Details").as[Details]
      tags         <- cursor.downField("Tags").as[Seq[String]]
      workoutData  <- cursor.downField("workoutData").as[NonEmptyList[WorkoutData]]
      intervalData <- cursor.downField("intervalData").as[NonEmptyList[IntervalData]]
      profileName  <- cursor.downField("ProfileName").as[String]
    } yield Workout(
      details = details,
      tags = tags,
      workoutData = workoutData,
      intervalData = intervalData,
      profileName = profileName,
    )

  implicit val zioDecoder: JsonDecoder[Workout] =
    DeriveJsonDecoder.gen[Workout]

}
