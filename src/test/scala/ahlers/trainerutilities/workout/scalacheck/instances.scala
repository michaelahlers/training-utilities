package ahlers.trainerutilities.workout.scalacheck

import ahlers.trainerutilities.workout.IntervalDetails
import cats.data.NonEmptyChain
import cats.data.scalacheck.instances._
import com.softwaremill.quicklens._
import magnolify.scalacheck.semiauto._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import trainerroad.schema.web.IntervalData
import trainerroad.schema.web.WorkoutData
import trainerroad.schema.web.scalacheck.instances._

object instances {

  /** Arbitrary within valid and reasonable limits. */
  implicit val arbIntervalDetails: Arbitrary[IntervalDetails] = Arbitrary(for {
    interval <- arbitrary[IntervalData]

    details <- {
      val genDetail =
        for {
          centiseconds <- Gen.choose(interval.start * 100, interval.end * 100)
          detail <- arbitrary[WorkoutData]
        } yield detail
          .modify(_.centiseconds)
          .setTo(centiseconds)

      for {
        head <- genDetail
        tail <- Gen.listOf(genDetail)
      } yield NonEmptyChain.of(
        head = head,
        tail = tail: _*,
      )
    }

  } yield IntervalDetails(
    interval = interval,
    details = details,
  ))

}
