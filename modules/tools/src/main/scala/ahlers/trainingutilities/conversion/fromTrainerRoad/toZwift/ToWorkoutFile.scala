package ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift

import cats.data.Validated
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutDetails
import zwift.schema.desktop.WorkoutFile

object ToWorkoutFile {

  def from(workout: Workout): Validated[Throwable, WorkoutFile] =
    for {
      steps <- ToWorkoutSteps.from(workout.workoutData)
    } yield WorkoutFile(
      author = "TrainerRoad",
      name = workout.details.workoutName,
      description = workout.details.workoutDescription,
      sportType = "bike",
      tags = workout.tags.map(WorkoutFile.Tag(_)),
      workout = steps,
    )

  def from(workout: WorkoutDetails): Validated[Throwable, WorkoutFile] =
    from(workout.workout)

}
