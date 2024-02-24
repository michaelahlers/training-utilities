package trainerroad.schema.web.diffx

import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.generic.auto._
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData

object instances {

  implicit val diffIntervalData: Diff[IntervalData] = Diff.summon
  implicit val diffWorkoutData: Diff[WorkoutData]   = Diff.summon

}
