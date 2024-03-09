package trainerroad.schema.web

import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class Details(
  @jsonField("Id") id: Int,
  @jsonField("WorkoutName") workoutName: String,
  @jsonField("WorkoutDescription") workoutDescription: String,
)

object Details {

  implicit val zioDecoder: JsonDecoder[Details] =
    DeriveJsonDecoder.gen[Details]

}
