package trainerroad.schema.web.scalacheck

import magnolify.scalacheck.semiauto._
import org.scalacheck.Arbitrary
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData

object instances {

  implicit val arbIntervalData: Arbitrary[IntervalData] = ArbitraryDerivation[IntervalData]
  implicit val arbWorkoutData: Arbitrary[WorkoutData] = ArbitraryDerivation[WorkoutData]

}
