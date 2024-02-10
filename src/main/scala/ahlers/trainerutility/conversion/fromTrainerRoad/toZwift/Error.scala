package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import trainerroad.schema.web.WorkoutData

sealed trait Error
object Error {
  case object NoWorkoutsForStep extends Error
  case class UndefinedSlope(workouts: Seq[WorkoutData]) extends Error
}
