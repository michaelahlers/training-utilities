package ahlers.trainerutilities.workout.diffx

import ahlers.trainerutilities.workout.Error
import ahlers.trainerutilities.workout.IntervalDetails
import com.softwaremill.diffx._
import com.softwaremill.diffx.cats.instances._
import com.softwaremill.diffx.generic.auto._
import trainerroad.schema.web.diffx.instances._

object instances {

  implicit val diffError: Diff[Error] = Diff.summon
  implicit val diffIntervalDetails: Diff[IntervalDetails] = Diff.summon
}
