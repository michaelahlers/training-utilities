package ahlers.training.tools.convert.vendor

import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutCliApp.Settings
import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutCliApp.WithDocumentsFolder
import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutCliApp.WithSettings
import ahlers.training.tools.convert.vendor.diffx.instances._
import better.files.File
import zio.ZIO
import zio.diffx.DiffxAssertions._
import zio.test.Assertion.equalTo
import zio.test._

object TrainerRoadWorkoutZwiftWorkoutCliAppSpec extends ZIOSpecDefault {

  object WithDocumentsFolderSpec extends ZIOSpecDefault {

    val forMacOS: Spec[WithDocumentsFolder, Throwable] = test("for macOS") {
      for {
        live <- ZIO.service[WithDocumentsFolder]
      } yield assert(live.documentsFolder)(equalTo(File.home / "Documents"))
    } @@ TestAspect.mac

    val forWindows: Spec[WithDocumentsFolder, Throwable] = test("for Windows") {
      for {
        live <- ZIO.service[WithDocumentsFolder]
      } yield assert(live.documentsFolder)(equalTo(File.home / "Documents"))
    } @@ TestAspect.windows

    override val spec = suite("WithDocumentsFolder")(
      forMacOS,
      forWindows,
    ).provide(WithDocumentsFolder.live)

  }

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
    WithDocumentsFolderSpec.spec,
    WithSettingsSpec.spec,
  )

}
