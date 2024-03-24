package cats.data.scalacheck

import cats.data.NonEmptyList
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object instances {

  implicit def arbNonEmptyList[A: Arbitrary]: Arbitrary[NonEmptyList[A]] = Arbitrary(for {
    head <- arbitrary[A]
    tail <- arbitrary[List[A]]
  } yield NonEmptyList(
    head = head,
    tail = tail,
  ))

}
