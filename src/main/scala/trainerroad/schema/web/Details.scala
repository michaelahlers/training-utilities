package trainerroad.schema.web

import io.circe.Decoder

case class Details(
  workoutName: String,
  workoutDescription: String,
)

object Details {

  implicit val decoder: Decoder[Details] = cursor =>
    for {
      name <- cursor.downField("WorkoutName").as[String]
      description <- cursor.downField("WorkoutDescription").as[String]
    } yield Details(
      workoutName = name,
      workoutDescription = description,
    )

}
