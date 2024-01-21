package ahlers.trainerutilities.workout.scalacheck

import ahlers.trainerutilities.workout.IntervalDetails
import cats.data.scalacheck.instances._
import magnolify.scalacheck.semiauto._
import org.scalacheck.Arbitrary
import trainerroad.schema.web.scalacheck.instances._

object instances {

  implicit val arbIntervalDetails: Arbitrary[IntervalDetails] = ArbitraryDerivation[IntervalDetails]

}
