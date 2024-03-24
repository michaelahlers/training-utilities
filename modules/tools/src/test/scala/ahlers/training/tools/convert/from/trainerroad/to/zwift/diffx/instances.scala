package ahlers.training.tools.convert.from.trainerroad.to.zwift.diffx

import ahlers.training.tools.convert.from.trainerroad.to.zwift.WorkoutCliApp
import com.softwaremill.diffx._
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffTrainerRoadWorkoutZwiftWorkoutCliAppSettings: Diff[WorkoutCliApp.Settings] = Diff.summon

}
