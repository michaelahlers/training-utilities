package trainerroad.schema.web

import io.circe.Decoder

/**
 * @param centiseconds In serialization, the field is called `seconds`, but these are actually hundredths. For example, if an [[IntervalData]] covers [[IntervalData.start 180]] to [[IntervalData.end 300]], then relevant [[WorkoutData]] range from [[WorkoutData.centiseconds 18,000]] to [[WorkoutData.centiseconds 30,000]].
 */
case class WorkoutData(
  centiseconds: Int,
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
      centiseconds = seconds,
      memberFtpPercent = memberFtpPercent,
      ftpPercent = ftpPercent,
    )

}
