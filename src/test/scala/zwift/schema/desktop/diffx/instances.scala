package zwift.schema.desktop.diffx

import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._
import zwift.schema.desktop.WorkoutStep
import zwift.schema.desktop.WorkoutStep.Ramp
import zwift.schema.desktop.WorkoutStep.SteadyState

object instances {

  implicit val diffSteadyState: Diff[SteadyState] = Diff.summon
  implicit val diffRamp: Diff[Ramp] = Diff.summon
  implicit val diffWorkoutStep: Diff[WorkoutStep] = Diff.summon

}
