package trainerroad.schema.web

import io.circe.Decoder

/**
 * @param milliseconds In serialization, the field field is misnamed compared to [[IntervalData.start]].
 */
case class WorkoutData(
  milliseconds: Int,
  memberFtpPercent: Float,
  ftpPercent: Float,
)

object WorkoutData {

  implicit val decoder: Decoder[WorkoutData] = cursor =>
    for {
      seconds <- cursor.downField("seconds").as[Int]
      memberFtpPercent <- cursor.downField("memberFtpPercent").as[Float]
      ftpPercent <- cursor.downField("ftpPercent").as[Float]
    } yield WorkoutData(
      milliseconds = seconds,
      memberFtpPercent = memberFtpPercent,
      ftpPercent = ftpPercent,
    )

}
