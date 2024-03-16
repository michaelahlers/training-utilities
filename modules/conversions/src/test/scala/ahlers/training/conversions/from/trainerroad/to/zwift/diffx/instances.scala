package ahlers.training.conversions.from.trainerroad.to.zwift.diffx

import ahlers.training.conversions.from.trainerroad.to.zwift.NoIntervalsInWorkoutException
import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffNoIntervalsInWorkoutException: Diff[NoIntervalsInWorkoutException] = Diff.summon

}
