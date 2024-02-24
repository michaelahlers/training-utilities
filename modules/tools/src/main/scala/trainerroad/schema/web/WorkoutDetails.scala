package trainerroad.schema.web

import io.circe.Decoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class WorkoutDetails(
  @jsonField("Workout") workout: Workout,
)

object WorkoutDetails {

  implicit val circeDecoder: Decoder[WorkoutDetails] = cursor =>
    for {
      workout <- cursor.downField("Workout").as[Workout]
    } yield WorkoutDetails(
      workout = workout,
    )

  implicit val zioDecoder: JsonDecoder[WorkoutDetails] =
    DeriveJsonDecoder.gen[WorkoutDetails]

}
