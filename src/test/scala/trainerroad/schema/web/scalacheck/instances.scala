package trainerroad.schema.web.scalacheck

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData

object instances {

  /** Arbitrary within valid and reasonable limits. */
  implicit val arbIntervalData: Arbitrary[IntervalData] = Arbitrary(for {
    name <- Gen.identifier
    start <- Gen.posNum[Int]
    end <- Gen.posNum[Int].map(_ + start)
    isFake <- arbitrary[Boolean]
    startTargetPowerPercent <- Gen.choose(0.0f, 100.0f)
  } yield IntervalData(
    name = name,
    start = start,
    end = end,
    isFake = isFake,
    startTargetPowerPercent = startTargetPowerPercent,
  ))

  /** Arbitrary within valid and reasonable limits. */
  implicit val arbWorkoutData: Arbitrary[WorkoutData] = Arbitrary(for {
    centiseconds <- Gen.posNum[Int]
    memberFtpPercent <- Gen.choose(0.0f, 100.0f)
    ftpPercent <- Gen.choose(0.0f, 100.0f)
  } yield WorkoutData(
    centiseconds = centiseconds,
    memberFtpPercent = memberFtpPercent,
    ftpPercent = ftpPercent,
  ))

}
