package ahlers.trainerutilities.workout

import trainerroad.schema.web.IntervalData

sealed trait Error
object Error {
  case class NoDetailsForInterval(interval: IntervalData) extends Error
}
