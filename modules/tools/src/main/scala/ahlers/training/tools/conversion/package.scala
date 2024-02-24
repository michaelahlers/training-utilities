package ahlers.training.tools

package object conversion {

  type TrainerRoadWorkout = trainerroad.schema.web.Workout
  val TrainerRoadWorkout = trainerroad.schema.web.Workout

  type ZwiftWorkout = zwift.schema.desktop.WorkoutFile
  val ZwiftWorkout = zwift.schema.desktop.WorkoutFile

}
