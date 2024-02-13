package ahlers.trainingutilities.conversion

import better.files.File
import zio.test._

object ConversionSettingsSpec extends ZIOSpecDefault {

  val forMacOS = test("for macOS") {
    for {
      settings <- ConversionSettings.load
    } yield assertTrue {
      settings == ConversionSettings(
        environment = ConversionSettings.Environment(
          home = File.home.pathAsString,
          windows = ConversionSettings.Environment.Windows(
            oneDrive = None,
          ),
        ),
      )
    }
  } @@ TestAspect.mac

  val forWindows = test("for Windows") {
    for {
      settings <- ConversionSettings.load
    } yield assertTrue {
      settings == ConversionSettings(
        environment = ConversionSettings.Environment(
          home = File.home.pathAsString,
          windows = ConversionSettings.Environment.Windows(
            oneDrive = None,
          ),
        ),
      )
    }
  } @@ TestAspect.windows

  override val spec = suite("ConversionSettings")(
    forMacOS,
    forWindows,
  )
}
