package zio.diffx

import com.softwaremill.diffx._
import zio.test._

trait DiffxAssertions {

  def matchesTo[A: Diff](expected: A): TestArrow[A, Boolean] =
    TestArrow
      .make[A, Boolean] { actual =>
        val result = Diff.compare(expected, actual)
        if (result.isIdentical) TestTrace.succeed(true)
        else TestTrace.fail(result.show())
      }

}

object DiffxAssertions extends DiffxAssertions
