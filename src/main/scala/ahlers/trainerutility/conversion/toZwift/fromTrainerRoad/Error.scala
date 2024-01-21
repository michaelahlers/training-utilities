package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad

import trainerroad.schema.web.IntervalData

sealed trait Error
object Error {
  case class NoWorkoutsForInterval(interval: IntervalData) extends Error
}
