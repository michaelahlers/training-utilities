package trainerroad.schema.web

case class IntervalData(
  Name: String,
  Start: Int,
  End: Int,
  IsFake: Boolean,
  StartTargetPowerPercent: Float,
)
