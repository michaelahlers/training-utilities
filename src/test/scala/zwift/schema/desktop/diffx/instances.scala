package zwift.schema.desktop.diffx

import com.softwaremill.diffx.Diff
import zwift.schema.desktop.WorkoutStep
import com.softwaremill.diffx.generic.auto._


object instances {

  implicit val diffWorkoutStep:Diff[WorkoutStep] = Diff.summon

}
