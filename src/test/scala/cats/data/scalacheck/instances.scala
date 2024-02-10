package cats.data.scalacheck

import cats.data.NonEmptyChain
import cats.data.NonEmptyList
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object instances {

  implicit def arbNonEmptyChain[A: Arbitrary]: Arbitrary[NonEmptyChain[A]] = Arbitrary(for {
    head <- arbitrary[A]
    tail <- arbitrary[Seq[A]]
  } yield NonEmptyChain(
    a = head,
    as = tail: _*,
  ))

  implicit def arbNonEmptyList[A: Arbitrary]: Arbitrary[NonEmptyList[A]] = Arbitrary(for {
    head <- arbitrary[A]
    tail <- arbitrary[List[A]]
  } yield NonEmptyList(
    head = head,
    tail = tail,
  ))

}
