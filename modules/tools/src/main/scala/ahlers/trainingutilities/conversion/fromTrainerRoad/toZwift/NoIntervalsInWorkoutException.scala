package ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift

import cats.data.NonEmptyList
import trainerroad.schema.web.WorkoutData

case class NoIntervalsInWorkoutException(workouts: NonEmptyList[WorkoutData]) extends IllegalArgumentException
