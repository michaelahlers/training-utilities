package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad

import ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.Error.NoWorkoutsForInterval
import cats.data.Validated.Invalid
import cats.data.diffx.instances._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher._
import diffx.instances._
import org.scalatest.wordspec.AnyWordSpec
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData
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

    "interval ends before workouts" in {
      val interval: IntervalData = IntervalData(
        name = null,
        start = 0,
        end = 100,
        isFake = false,
        startTargetPowerPercent = 0,
      )

      WorkoutStepFrom(
        interval = interval,
        workouts = Seq(
          WorkoutData(
            centiseconds = interval.end * 100 + 1,
            memberFtpPercent = 0,
            ftpPercent = 0,
          ),
        ),
      ).shouldMatchTo(Invalid(NoWorkoutsForInterval(
        interval = interval,
      )))
    }

  }

}
