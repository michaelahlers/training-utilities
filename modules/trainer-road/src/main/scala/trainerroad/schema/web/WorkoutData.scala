package trainerroad.schema.web

import squants.time.Milliseconds
import squants.time.Time
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

/**
 * @param milliseconds In serialization, the field field is misnamed compared to [[IntervalData.start]].
 */
case class WorkoutData(
  @jsonField("seconds") offset: Time,
  memberFtpPercent: Float,
  ftpPercent: Float,
)

object WorkoutData {

  implicit val zioDecoder: JsonDecoder[WorkoutData] = {
    implicit val zioDecoderTime: JsonDecoder[Time] = JsonDecoder[Int].map(Milliseconds(_))
    DeriveJsonDecoder.gen[WorkoutData]
  }

}
