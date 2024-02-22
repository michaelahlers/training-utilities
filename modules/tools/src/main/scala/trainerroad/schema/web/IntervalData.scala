package trainerroad.schema.web

import io.circe.Decoder

case class IntervalData(
  name: String,
  start: Int,
  end: Int,
  isFake: Boolean,
  startTargetPowerPercent: Float,
) {

  require(
    start <= end,
    s"Start ($start) must be less than or equal to end ($end).",
  )

}

object IntervalData {

  implicit val decoder: Decoder[IntervalData] = cursor =>
    for {
      name                    <- cursor.downField("Name").as[String]
      start                   <- cursor.downField("Start").as[Int]
      end                     <- cursor.downField("End").as[Int]
      isFake                  <- cursor.downField("IsFake").as[Boolean]
      startTargetPowerPercent <- cursor.downField("StartTargetPowerPercent").as[Float]
    } yield IntervalData(
      name = name,
      start = start,
      end = end,
      isFake = isFake,
      startTargetPowerPercent = startTargetPowerPercent,
    )

}
