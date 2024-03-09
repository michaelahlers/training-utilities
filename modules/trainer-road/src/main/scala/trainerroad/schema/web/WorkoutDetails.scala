package trainerroad.schema.web

import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class WorkoutDetails(
  @jsonField("Workout") workout: Workout,
)

object WorkoutDetails {

  implicit val zioDecoder: JsonDecoder[WorkoutDetails] =
    DeriveJsonDecoder.gen[WorkoutDetails]

}
