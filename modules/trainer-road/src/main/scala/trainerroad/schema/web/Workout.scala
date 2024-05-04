package trainerroad.schema.web

import cats.data.NonEmptyList
import cats.data.zio.json.instances._
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class Workout(
  @jsonField("Details") details: Details,
  @jsonField("Tags") tags: Seq[String],
  @jsonField("WorkoutData") workoutData: NonEmptyList[WorkoutData],
  @jsonField("IntervalData") intervalData: NonEmptyList[IntervalData],
  @jsonField("ProfileName") profileName: String,
)

object Workout {

  implicit val zioDecoder: JsonDecoder[Workout] =
    DeriveJsonDecoder.gen[Workout]

}
