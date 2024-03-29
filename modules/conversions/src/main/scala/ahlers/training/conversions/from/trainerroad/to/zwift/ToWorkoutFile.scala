package ahlers.training.conversions.from.trainerroad.to.zwift

import cats.data.NonEmptyList
import cats.data.Validated
import cats.implicits._
import org.jsoup.Jsoup
import scala.util.control.NoStackTrace
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutDetails
import zwift.schema.desktop.WorkoutFile
import zwift.schema.desktop.WorkoutStep

object ToWorkoutFile {

  /** @todo Make this a whole lot more simple. */
  def from(workout: Workout): Validated[Throwable, WorkoutFile] = {

    val stepsF: Validated[NonEmptyList[Throwable], NonEmptyList[WorkoutStep]] =
      ToWorkoutSteps.from(workout.workoutData)
        .toValidatedNel

    val nameF: Validated[NonEmptyList[Throwable], String] = Validated.catchNonFatal(Jsoup
      .parse(workout.details.workoutName)
      .root()
      .text()
      .trim)
      .toValidatedNel

    val descriptionF: Validated[NonEmptyList[Throwable], String] = Validated.catchNonFatal(Jsoup
      .parse(workout.details.workoutDescription)
      .root()
      .text()
      .trim)
      .toValidatedNel

    (stepsF, nameF, descriptionF)
      .mapN { (steps, name, description) =>
        val tags =
          workout
            .tags
            .map(WorkoutFile.Tag(_)) :+
            WorkoutFile.Tag(workout.profileName)

        WorkoutFile(
          author = "TrainerRoad",
          name = name,
          description = description,
          sportType = "bike",
          tags = tags,
          workout = steps,
        )
      }
      .leftMap(_
        .foldLeft(new IllegalArgumentException("Couldn't convert TrainerRoad workout to Zwift workout.") with NoStackTrace) { (error, next) =>
          error.addSuppressed(next)
          error
        })
  }

  def from(workout: WorkoutDetails): Validated[Throwable, WorkoutFile] =
    from(workout.workout)

}
