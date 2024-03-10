package ahlers.training.tools.convert.vendor

import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutCliApp.Settings
import ahlers.training.tools.convert.vendor.diffx.instances._
import better.files.File
import zio.diffx.DiffxAssertions._
import zio.test._

object TrainerRoadWorkoutZwiftWorkoutCliAppSpec extends ZIOSpecDefault {

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
