package trainerroad.schema.web

import io.circe.Decoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class Details(
  @jsonField("Id") id: Int,
  @jsonField("WorkoutName") workoutName: String,
  @jsonField("WorkoutDescription") workoutDescription: String,
)

object Details {

  implicit val decoder: Decoder[Details] = cursor =>
    for {
      id          <- cursor.downField("Id").as[Int]
      name        <- cursor.downField("WorkoutName").as[String]
      description <- cursor.downField("WorkoutDescription").as[String]
    } yield Details(
      id = id,
      workoutName = name,
      workoutDescription = description,
    )

  implicit val zioDecoder: JsonDecoder[Details] =
    DeriveJsonDecoder.gen[Details]

}
