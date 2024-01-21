package ahlers.trainerutilities.workout

import ahlers.trainerutilities.workout.Error.NoDetailsForInterval
import ahlers.trainerutilities.workout.diffx.instances._
import cats.data.Validated.Invalid
import cats.data.diffx.instances._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher._
import org.scalatest.wordspec.AnyWordSpec
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData

class IntervalDetailsSpec extends AnyWordSpec {

  "Indicate no details for interval" when {

    "details are empty" in {
      val interval: IntervalData = null

      IntervalDetails
        .fromSeq(
          interval = interval,
          details = Nil,
        )
        .shouldMatchTo(Invalid(NoDetailsForInterval(
          interval = interval,
        )))
    }

    "interval end is too early" in {
      val interval: IntervalData = IntervalData(
        name = null,
        start = 0,
        end = 100,
        isFake = false,
        startTargetPowerPercent = 0,
      )

      IntervalDetails
        .fromSeq(
          interval = interval,
          details = Seq(
            WorkoutData(
              centiseconds = interval.end * 100 + 1,
              memberFtpPercent = 0,
              ftpPercent = 0,
            ),
          ),
        )
        .shouldMatchTo(Invalid(NoDetailsForInterval(
          interval = interval,
        )))
    }

  }

}
