package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift

import Error.NoWorkoutsForInterval
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.diffx.instances._
import com.softwaremill.diffx.scalatest.DiffShouldMatcher._
import diffx.instances._
import org.scalactic.anyvals.NonEmptyList
import org.scalatest.wordspec.AnyWordSpec
import squants.time.Seconds
import squants.time.Time
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.Workout
import trainerroad.schema.web.WorkoutData
import trainerroad.schema.web.diffx.instances._
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup
import zwift.schema.desktop.diffx.instances._
import zwift.schema.desktop.scalacheck.instances._

class ToWorkoutStepSpec extends AnyWordSpec {
  import ToWorkoutStepSpec.toWorkoutData

  // "No workouts for interval" when {
  //
  //  "workouts are empty" in {
  //
  //    ToWorkoutStep(
  //      workouts = Seq.empty,
  //    ).shouldMatchTo(Invalid(NoWorkoutsForInterval))
  //  }
  //
  // }
  //
  // "Steady state workout step" when {
  //
  //  "interior interval" that {
  //
  //    "specifies constant power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 5,
  //        end = 10,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 50,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        (interval.end until interval.end + 5).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 0,
  //          )
  //        }
  //
  //      val step: SteadyState = SteadyState(
  //        durationSeconds = 5,
  //        ftpPowerRatio = 0.5f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //  }
  //
  // }
  //
  // "Ramp workout step" when {
  //
  //  "interior interval" that {
  //
  //    "specifies increasing power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 5,
  //        end = 10,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          val ftpPercent = 50 + second - interval.start
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = ftpPercent,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        (interval.end until interval.end + 5).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 0,
  //          )
  //        }
  //
  //      val step: Ramp = Ramp(
  //        durationSeconds = 5,
  //        ftpPowerLowRatio = 0.5f,
  //        ftpPowerHighRatio = 0.54f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //    "specifies decreasing power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 5,
  //        end = 10,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          val ftpPercent = 50 - second + interval.start
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = ftpPercent,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        (interval.end until interval.end + 5).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 100,
  //            ftpPercent = 100,
  //          )
  //        }
  //
  //      val step: Ramp = Ramp(
  //        durationSeconds = 5,
  //        ftpPowerLowRatio = 0.46f,
  //        ftpPowerHighRatio = 0.5f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //  }
  //
  // }
  //
  // "Warmup workout step" when {
  //
  //  "first interval" that {
  //
  //    "specifies constant power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 0,
  //        end = 5,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 50,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        (interval.end until interval.end + 5).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 0,
  //          )
  //        }
  //
  //      val step: Warmup = Warmup(
  //        durationSeconds = 5,
  //        ftpPowerLowRatio = 0.5f,
  //        ftpPowerHighRatio = 0.5f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //    "specifies increasing power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 0,
  //        end = 5,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          val ftpPercent = 50 + second - interval.start
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = ftpPercent,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        (interval.end until interval.end + 5).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 0,
  //          )
  //        }
  //
  //      val step: Warmup = Warmup(
  //        durationSeconds = 5,
  //        ftpPowerLowRatio = 0.5f,
  //        ftpPowerHighRatio = 0.54f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //  }
  //
  // }
  //
  // "Cool-down workout step" when {
  //
  //  "last interval" that {
  //
  //    "specifies constant power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 5,
  //        end = 10,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 50,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        Seq(
  //          WorkoutData(
  //            milliseconds = interval.end * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 0,
  //          ),
  //        )
  //
  //      val step: Cooldown = Cooldown(
  //        durationSeconds = 5,
  //        ftpPowerLowRatio = 0.5f,
  //        ftpPowerHighRatio = 0.5f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //    "specifies decreasing power" in {
  //      val interval: IntervalData = IntervalData(
  //        name = null,
  //        start = 5,
  //        end = 10,
  //        isFake = false,
  //        startTargetPowerPercent = 0,
  //      )
  //
  //      val current: Seq[WorkoutData] =
  //        (interval.start until interval.end).map { second =>
  //          val ftpPercent = 50 - second + interval.start
  //          WorkoutData(
  //            milliseconds = second * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = ftpPercent,
  //          )
  //        }
  //
  //      val next: Seq[WorkoutData] =
  //        Seq(
  //          WorkoutData(
  //            milliseconds = interval.end * 1000,
  //            memberFtpPercent = 0,
  //            ftpPercent = 0,
  //          ),
  //        )
  //
  //      val step: Cooldown = Cooldown(
  //        durationSeconds = 5,
  //        ftpPowerLowRatio = 0.46f,
  //        ftpPowerHighRatio = 0.5f,
  //      )
  //
  //      ToWorkoutStep(
  //        workouts = current ++ next,
  //      ).shouldMatchTo(Valid((step, next)))
  //    }
  //
  //  }
  //
  // }

  pprint.log(toWorkoutData(Seq(
    Ramp(
      durationSeconds = 5,
      ftpRatioStart = 0.5f,
      ftpRatioEnd = 1.0f,
    ),
    Ramp(
      durationSeconds = 5,
      ftpRatioStart = 1.0f,
      ftpRatioEnd = 0.5f,
    ),
  )))

}

object ToWorkoutStepSpec {

  def toFtpRatios(
    step: WorkoutStep,
    isLast: Boolean,
  ): Seq[Float] = {
    import step.ftpRatioStart
    import step.ftpRatioEnd

    /**
     * The number of step slices (each [[WorkoutData]] is equal to the duration in seconds of [[step]].
     * However, there's a special case as [[Workout.workoutData]] includes "terminator" slice with the final power.
     */
    val slices =
      if (isLast) 0 to step.durationSeconds
      else 0 until step.durationSeconds

    val ftpDelta: Float = ftpRatioEnd - ftpRatioStart
    val ftpStep: Float = ftpDelta / (slices.size - 1)

    slices.map { slice =>
      ftpRatioStart + ftpStep * slice
    }
  }

  def toWorkoutData(
    steps: Seq[WorkoutStep],
  ): Seq[WorkoutData] = {
    val lastIndex = steps.size - 1

    /** Not efficient, but that's fine. */
    steps
      .zipWithIndex
      .flatMap { case (step, index) =>
        val isLast = lastIndex == index
        toFtpRatios(
          step = step,
          isLast = isLast,
        )
      }
      .zipWithIndex
      .map { case (ftpRatio, offsetSeconds) =>
        WorkoutData(
          milliseconds = offsetSeconds * 1000,
          memberFtpPercent = 0,
          ftpPercent = ftpRatio * 100,
        )
      }
  }

}
