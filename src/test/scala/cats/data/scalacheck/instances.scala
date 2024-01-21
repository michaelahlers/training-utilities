package cats.data.scalacheck

import cats.data.NonEmptyChain
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object instances {

  implicit def arbNonEmptyChain[A: Arbitrary]: Arbitrary[NonEmptyChain[A]] = Arbitrary(for {
    head <- arbitrary[A]
    tail <- arbitrary[Seq[A]]
  } yield NonEmptyChain.of(
    head = head,
    tail = tail: _*,
  ))

}
