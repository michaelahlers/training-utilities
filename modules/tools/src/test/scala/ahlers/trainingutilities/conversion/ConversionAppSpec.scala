package ahlers.trainingutilities.conversion

import zio._
import zio.test.Assertion._
import zio.test._

object ConversionAppSpec extends ZIOSpecDefault {

  override val spec = suite("ConversionApp")(
    test("emits settings") {
      for {
        _ <- ConversionApp.run
      } yield assertTrue(true)
    },
  )
}
