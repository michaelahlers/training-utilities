package ahlers.training.tools

package object conversion {

  type TrainerRoadWorkout = trainerroad.schema.web.WorkoutDetails
  val TrainerRoadWorkout = trainerroad.schema.web.WorkoutDetails

  type ZwiftWorkout = zwift.schema.desktop.WorkoutFile
  val ZwiftWorkout = zwift.schema.desktop.WorkoutFile

}
