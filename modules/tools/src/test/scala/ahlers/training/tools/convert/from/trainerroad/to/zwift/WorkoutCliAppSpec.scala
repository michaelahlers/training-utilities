package ahlers.training.tools.convert.from.trainerroad.to.zwift

import ahlers.training.tools.convert.from.trainerroad.to.zwift.WorkoutCliApp.Settings
import ahlers.training.tools.convert.from.trainerroad.to.zwift.WorkoutCliApp.WithSettings
import ahlers.training.tools.convert.from.trainerroad.to.zwift.diffx.instances._
import better.files.File
import zio.ZIO
import zio.diffx.DiffxAssertions._
import zio.test._

object WorkoutCliAppSpec extends ZIOSpecDefault {

  object WithSettingsSpec extends ZIOSpecDefault {

    val forMacOS: Spec[WithSettings, Throwable] = test("for macOS") {
      for {
        live <- ZIO.service[WithSettings]
      } yield assert(live.settings)(matchesTo {
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

    val forWindows: Spec[WithSettings, Throwable] = test("for Windows") {
      for {
        live <- ZIO.service[WithSettings]
      } yield assert(live.settings)(matchesTo {
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

    override val spec = suite("WithSettings")(
      forMacOS,
      forWindows,
    ).provide(WithSettings.live)

  }

  override val spec = suite("TrainerRoadWorkoutZwiftWorkoutCliApp")(
    WithSettingsSpec.spec,
  )

}
