package ahlers.trainingutilities.conversion

import ahlers.trainingutilities.conversion.diffx.instances._
import better.files.File
import zio.diffx.DiffxAssertions._
import zio.test._

object ConversionSettingsSpec extends ZIOSpecDefault {

  val forMacOS = test("for macOS") {
    for {
      settings <- ConversionSettings.load
    } yield assert(settings)(matchesTo {
      ConversionSettings(
        environment = ConversionSettings.Environment(
          home = File.home.pathAsString,
          windows = ConversionSettings.Environment.Windows(
            oneDrive = None,
          ),
        ),
      )
    })
  } @@ TestAspect.mac

  val forWindows = test("for Windows") {
    for {
      settings <- ConversionSettings.load
    } yield assert(settings)(matchesTo {
      ConversionSettings(
        environment = ConversionSettings.Environment(
          home = File.home.pathAsString,
          windows = ConversionSettings.Environment.Windows(
            oneDrive = None,
          ),
        ),
      )
    })
  } @@ TestAspect.windows

  override val spec = suite("ConversionSettings")(
    forMacOS,
    forWindows,
  )
}
