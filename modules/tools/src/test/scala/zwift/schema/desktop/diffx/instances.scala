package zwift.schema.desktop.diffx

import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._
import zwift.schema.desktop
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Cooldown
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState
import zwift.schema.desktop.WorkoutStep.Warmup

object instances {

  implicit val diffWorkoutStepWarmup: Diff[WorkoutStep.Warmup] = Diff.summon
  implicit val diffWorkoutStepSteadyState: Diff[WorkoutStep.SteadyState] = Diff.summon
  implicit val diffWorkoutStepRamp: Diff[WorkoutStep.Ramp] = Diff.summon
  implicit val diffWorkoutStepCooldown: Diff[WorkoutStep.Cooldown] = Diff.summon

  implicit val diffWorkoutStep: Diff[WorkoutStep] = Diff.summon[WorkoutStep]
//.modify(_.ftpRatioStart)
//.setTo(Diff.approximate(1f))
//.modify(_.ftpRatioEnd)
//.setTo(Diff.approximate(1f))

}
