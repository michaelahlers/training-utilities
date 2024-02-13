package ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift

import cats.data.NonEmptyList
import trainerroad.schema.web.WorkoutData

sealed trait Error
object Error {
  case class NoIntervalsInWorkout(workouts: NonEmptyList[WorkoutData]) extends Error
}
