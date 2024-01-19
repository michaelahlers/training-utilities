package trainerroad.schema.web

import io.circe.Decoder

case class WorkoutData (
  seconds:Int,
  memberFtpPercent:Float,
  ftpPercent:Float,
                       )

object WorkoutData {

  implicit val decoder: Decoder[WorkoutData] = cursor =>
    for {
      seconds <- cursor.downField("seconds").as[Int]
      memberFtpPercent <- cursor.downField("memberFtpPercent").as[Float]
      ftpPercent <- cursor.downField("ftpPercent").as[Float]
    } yield WorkoutData(
      seconds = seconds,
      memberFtpPercent = memberFtpPercent,
      ftpPercent = ftpPercent,
    )

}
