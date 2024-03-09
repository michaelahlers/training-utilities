package trainerroad.schema.web

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

  implicit val zioDecoder: JsonDecoder[IntervalData] =
    DeriveJsonDecoder.gen[IntervalData]

}
