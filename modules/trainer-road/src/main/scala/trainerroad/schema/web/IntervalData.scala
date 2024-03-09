package trainerroad.schema.web

import io.circe.Decoder
import squants.time.Milliseconds
import squants.time.Time
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder
import zio.json.jsonField

case class IntervalData(
  @jsonField("Name") name: String,
  @jsonField("Start") start: Int,
  @jsonField("End") end: Int,
  @jsonField("IsFake") isFake: Boolean,
  @jsonField("StartTargetPowerPercent") startTargetPowerPercent: Float,
) {

  require(
    start <= end,
    s"Start ($start) must be less than or equal to end ($end).",
  )

}

object IntervalData {

  implicit val circeDecoder: Decoder[IntervalData] = cursor =>
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

  implicit val zioDecoder: JsonDecoder[IntervalData] =
    DeriveJsonDecoder.gen[IntervalData]

}
