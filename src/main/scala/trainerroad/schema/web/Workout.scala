package trainerroad.schema.web

import io.circe.Decoder

case class Workout(
  details: Details,
  intervalData: Seq[IntervalData],
)

object Workout {

  implicit val decoder: Decoder[Workout] = cursor =>
    for {
      details <- cursor.downField("Details").as[Details]
      intervalData <- cursor.downField("intervalData").as[Seq[IntervalData]]
    } yield Workout(
      details = details,
      intervalData = intervalData,
    )
}
