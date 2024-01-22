package ahlers.trainerutilities.workout.diffx

import ahlers.trainerutilities.workout.Error
import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffError: Diff[Error] = Diff.summon
}
