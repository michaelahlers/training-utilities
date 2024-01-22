package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad

import ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.Error.NoWorkoutsForInterval
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.diffx.instances._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher._
import diffx.instances._
import org.scalatest.wordspec.AnyWordSpec
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData
import trainerroad.schema.web.diffx.instances._
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.diffx.instances._

class WorkoutStepFromSpec extends AnyWordSpec {

  "Invalid no workouts for interval" when {

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
        start = 5,
        end = 10,
        isFake = false,
        startTargetPowerPercent = 0,
      )

      WorkoutStepFrom(
        interval = interval,
        workouts = Seq(
          WorkoutData(
            milliseconds = interval.end * 1000 + 1,
            memberFtpPercent = 0,
            ftpPercent = 0,
          ),
        ),
      ).shouldMatchTo(Invalid(NoWorkoutsForInterval(
        interval = interval,
      )))
    }

  }

  "Valid steady state workout step" when {

    "constant power" in {
      val interval: IntervalData = IntervalData(
        name = null,
        start = 0,
        end = 10,
        isFake = false,
        startTargetPowerPercent = 0,
      )

      val current: Seq[WorkoutData] =
        (interval.start until interval.end).map { second =>
          WorkoutData(
            milliseconds = second * 1000,
            memberFtpPercent = 0,
            ftpPercent = 50,
          )
        }

      val next: Seq[WorkoutData] =
        (interval.end until interval.end + 5).map { second =>
          WorkoutData(
            milliseconds = second * 1000,
            memberFtpPercent = 0,
            ftpPercent = 0,
          )
        }

      val step: SteadyState = SteadyState(
        durationSeconds = 10,
        ftpPowerRatio = 0.5f,
      )

      WorkoutStepFrom(
        interval = interval,
        workouts = current ++ next,
      ).shouldMatchTo(Valid((step, next)))
    }

  }

  "Valid ramp workout step" when {

    "increasing power" in {
      val interval: IntervalData = IntervalData(
        name = null,
        start = 0,
        end = 10,
        isFake = false,
        startTargetPowerPercent = 0,
      )

      val current: Seq[WorkoutData] =
        (interval.start until interval.end).map { second =>
          val ftpPercent = 50 + second + 0.1f * second
          WorkoutData(
            milliseconds = second * 1000,
            memberFtpPercent = 0,
            ftpPercent = ftpPercent,
          )
        }

      val next: Seq[WorkoutData] =
        (interval.end until interval.end + 5).map { second =>
          WorkoutData(
            milliseconds = second * 1000,
            memberFtpPercent = 0,
            ftpPercent = 0,
          )
        }

      val step: Ramp = Ramp(
        durationSeconds = 10,
        ftpPowerLowRatio = 0.5f,
        ftpPowerHighRatio = 0.6f,
      )

      WorkoutStepFrom(
        interval = interval,
        workouts = current ++ next,
      ).shouldMatchTo(Valid((step, next)))
    }

    "decreasing power" in {
      val interval: IntervalData = IntervalData(
        name = null,
        start = 0,
        end = 10,
        isFake = false,
        startTargetPowerPercent = 0,
      )

      val current: Seq[WorkoutData] =
        (interval.start until interval.end).map { second =>
          val ftpPercent = 50 - second - 0.1f * second
          WorkoutData(
            milliseconds = second * 1000,
            memberFtpPercent = 0,
            ftpPercent = ftpPercent,
          )
        }

      val next: Seq[WorkoutData] =
        (interval.end until interval.end + 5).map { second =>
          WorkoutData(
            milliseconds = second * 1000,
            memberFtpPercent = 100,
            ftpPercent = 100,
          )
        }

      val step: Ramp = Ramp(
        durationSeconds = 10,
        ftpPowerLowRatio = 0.4f,
        ftpPowerHighRatio = 0.5f,
      )

      WorkoutStepFrom(
        interval = interval,
        workouts = current ++ next,
      ).shouldMatchTo(Valid((step, next)))
    }

  }

}
