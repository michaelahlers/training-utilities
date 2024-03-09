package zwift.schema.desktop.diffx

import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._
import zwift.schema.desktop.WorkoutStep

object instances {

  implicit private val diffFloat: Diff[Float] = Diff.approximate[Float](0.1f)

  implicit val diffWorkoutStepWarmup: Diff[WorkoutStep.Warmup]           = Diff.summon
  implicit val diffWorkoutStepSteadyState: Diff[WorkoutStep.SteadyState] = Diff.summon
  implicit val diffWorkoutStepRamp: Diff[WorkoutStep.Ramp]               = Diff.summon
  implicit val diffWorkoutStepCooldown: Diff[WorkoutStep.Cooldown]       = Diff.summon

  implicit val diffWorkoutStep: Diff[WorkoutStep] = Diff.summon[WorkoutStep]

}
