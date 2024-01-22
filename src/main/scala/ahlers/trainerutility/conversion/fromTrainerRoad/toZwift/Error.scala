package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import trainerroad.schema.web.IntervalData

sealed trait Error
object Error {
  case class NoWorkoutsForInterval(interval: IntervalData) extends Error
}
