package ahlers.training.tools.convert.vendor.diffx

import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutCliApp
import com.softwaremill.diffx._
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffTrainerRoadWorkoutZwiftWorkoutCliAppSettings: Diff[TrainerRoadWorkoutZwiftWorkoutCliApp.Settings] = Diff.summon

}
