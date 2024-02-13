package ahlers.trainingutilities.conversion

import better.files.File
import zio._
import zio.test.Assertion._
import zio.test._

object ConversionSettingsSpec extends ZIOSpecDefault {

  val macOS = test("macOS") {
    for {
      settings <- ConversionSettings.load
    } yield assertTrue {
      settings == ConversionSettings(
        environment = ConversionSettings.Environment(
          macOS = ConversionSettings.Environment.MacOS(
            home = Some(File.home.pathAsString),
          ),
          windows = ConversionSettings.Environment.Windows(
            home = None,
            oneDrive = None,
          ),
        ),
      )
    }
  }

  override val spec = suite("ConversionSettings")(
    macOS,
  )
}
