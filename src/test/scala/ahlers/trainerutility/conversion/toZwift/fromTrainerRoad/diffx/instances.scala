package ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.diffx

import ahlers.trainerutility.conversion.toZwift.fromTrainerRoad.Error
import cats.data.diffx.instances._
import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._
import trainerroad.schema.web.diffx.instances._

object instances {

  implicit val diffError: Diff[Error] = Diff.summon

}
