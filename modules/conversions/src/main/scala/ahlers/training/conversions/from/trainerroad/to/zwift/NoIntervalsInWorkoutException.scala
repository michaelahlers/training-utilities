package ahlers.training.conversions.from.trainerroad.to.zwift

import cats.data.NonEmptyList
import trainerroad.schema.web.WorkoutData

case class NoIntervalsInWorkoutException(workouts: NonEmptyList[WorkoutData]) extends IllegalArgumentException
