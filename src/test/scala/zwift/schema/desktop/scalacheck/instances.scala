package zwift.schema.desktop.scalacheck

import magnolify.scalacheck.semiauto._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import zwift.schema.desktop.WorkoutStep

object instances {

  implicit val arbWorkoutStep: Arbitrary[WorkoutStep] = {
    implicit val arbInt: Arbitrary[Int] = Arbitrary(Gen.choose(5 * 60, 20 * 60))
    implicit val arbFloat: Arbitrary[Float] = Arbitrary(Gen.choose(40f, 120f))
    ArbitraryDerivation[WorkoutStep]
  }

}
