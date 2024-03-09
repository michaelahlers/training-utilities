package ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.diffx

import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.NoIntervalsInWorkoutException
import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffNoIntervalsInWorkoutException: Diff[NoIntervalsInWorkoutException] = Diff.summon

}
