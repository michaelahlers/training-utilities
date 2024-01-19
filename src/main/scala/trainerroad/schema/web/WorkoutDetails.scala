package trainerroad.schema.web

import io.circe.Decoder

case class WorkoutDetails(
  workout: Workout,
)

object WorkoutDetails {

  implicit val decoder: Decoder[WorkoutDetails] = cursor =>
    for {
      workout <- cursor.downField("Workout").as[Workout]
    } yield WorkoutDetails(
      workout = workout,
    )

}
