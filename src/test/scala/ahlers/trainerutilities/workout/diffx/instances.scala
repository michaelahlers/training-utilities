package ahlers.trainerutilities.workout.diffx

import ahlers.trainerutilities.workout.Error
import ahlers.trainerutilities.workout.IntervalDetails
import cats.data.diffx.instances._
import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._
import trainerroad.schema.web.diffx.instances._

object instances {

  implicit val diffError: Diff[Error] = Diff.summon
  implicit val diffIntervalDetails: Diff[IntervalDetails] = Diff.summon
}
