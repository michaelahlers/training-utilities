package zio.diffx

import com.softwaremill.diffx._
import zio.test._

trait DiffxAssertions {

  def matchesTo[A: Diff](expected: A): Assertion[A] =
    Assertion {
      TestArrow
        .make[A, Boolean] { actual =>
          val result = Diff.compare(expected, actual)
          TestTrace.boolean(result.isIdentical) {
            ErrorMessage.text(result.show())
          }
        }
    }

}

object DiffxAssertions extends DiffxAssertions
