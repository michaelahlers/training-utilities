package trainerroad.schema.web

import io.circe.Decoder

case class Details(
  id: Int,
  workoutName: String,
  workoutDescription: String,
)

object Details {

  implicit val decoder: Decoder[Details] = cursor =>
    for {
      id <- cursor.downField("Id").as[Int]
      name <- cursor.downField("WorkoutName").as[String]
      description <- cursor.downField("WorkoutDescription").as[String]
    } yield Details(
      id = id,
      workoutName = name,
      workoutDescription = description,
    )

}
