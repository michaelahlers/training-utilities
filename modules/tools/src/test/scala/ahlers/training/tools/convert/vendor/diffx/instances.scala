package ahlers.training.tools.convert.vendor.diffx

import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutApp
import com.softwaremill.diffx._
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffTrainerRoadWorkoutZwiftWorkoutAppSettings: Diff[TrainerRoadWorkoutZwiftWorkoutApp.Settings] = Diff.summon

}
