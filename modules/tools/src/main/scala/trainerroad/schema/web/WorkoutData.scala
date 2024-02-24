package trainerroad.schema.web

import io.circe.Decoder
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

  implicit val circeDecoder: Decoder[WorkoutData] = cursor =>
    for {
      seconds          <- cursor.downField("seconds").as[Int]
      memberFtpPercent <- cursor.downField("memberFtpPercent").as[Float]
      ftpPercent       <- cursor.downField("ftpPercent").as[Float]
    } yield WorkoutData(
      offset = Milliseconds(seconds),
      memberFtpPercent = memberFtpPercent,
      ftpPercent = ftpPercent,
    )

  implicit val zioDecoder: JsonDecoder[WorkoutData] = {
    implicit val zioDecoderTime: JsonDecoder[Time] = JsonDecoder[Int].map(Milliseconds(_))
    DeriveJsonDecoder.gen[WorkoutData]
  }

}
