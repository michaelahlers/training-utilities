package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import trainerroad.schema.web.IntervalData

sealed trait Error
object Error {
  case object NoWorkoutsForInterval extends Error
}
