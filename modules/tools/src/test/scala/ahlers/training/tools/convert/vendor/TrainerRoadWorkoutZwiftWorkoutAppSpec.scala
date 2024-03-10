package ahlers.training.tools.convert.vendor

import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutCliApp.Settings
import better.files.File
import diffx.instances._
import zio.diffx.DiffxAssertions._
import zio.test._

object TrainerRoadWorkoutZwiftWorkoutAppSpec extends ZIOSpecDefault {

  val forMacOS = test("for macOS") {
    for {
      settings <- Settings.load
    } yield assert(settings)(matchesTo {
      Settings(
        environment = Settings.Environment(
          home = File.home.pathAsString,
          windows = Settings.Environment.Windows(
            oneDrive = None,
          ),
        ),
      )
    })
  } @@ TestAspect.mac

  val forWindows = test("for Windows") {
    for {
      settings <- Settings.load
    } yield assert(settings)(matchesTo {
      Settings(
        environment = Settings.Environment(
          home = File.home.pathAsString,
          windows = Settings.Environment.Windows(
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
