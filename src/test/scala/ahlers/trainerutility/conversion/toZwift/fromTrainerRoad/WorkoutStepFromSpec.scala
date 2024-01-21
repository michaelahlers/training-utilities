package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad

import ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.Error.NoWorkoutsForInterval
import cats.data.Validated.Invalid
import cats.data.diffx.instances._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher._
import diffx.instances._
import org.scalatest.wordspec.AnyWordSpec
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.diffx.instances._
import zwift.schema.desktop.diffx.instances._

class WorkoutStepFromSpec extends AnyWordSpec {

  "Error with no workouts for interval" when {

    "workouts are empty" in {
      val interval: IntervalData = null

      WorkoutStepFrom(
        interval = interval,
        workouts = Seq.empty,
      ).shouldMatchTo(Invalid(NoWorkoutsForInterval(
        interval = interval,
      )))

    }

  }

}
