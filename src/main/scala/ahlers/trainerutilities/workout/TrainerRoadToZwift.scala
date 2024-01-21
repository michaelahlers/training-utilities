package ahlers.trainerutilities.workout

import cats.data.Validated
import trainerroad.schema.web.WorkoutDetails
import zwift.schema.desktop.WorkoutFile

object TrainerRoadToZwift extends (WorkoutDetails => Validated[Error, WorkoutFile]) {

  override def apply(workoutDetails: WorkoutDetails) = ???

}
