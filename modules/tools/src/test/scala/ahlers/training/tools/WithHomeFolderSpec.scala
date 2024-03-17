package ahlers.training.tools

import better.files.File
import zio.ZIO
import zio.test.Assertion.equalTo
import zio.test.Spec
import zio.test.ZIOSpecDefault
import zio.test.assert

object WithHomeFolderSpec extends ZIOSpecDefault {

  val homeFolder: Spec[Any, Throwable] = test("homeFolder") {
    for {
      live <- ZIO.service[WithHomeFolder]
    } yield assert(live.homeFolder)(equalTo(File.home))
  } provide WithHomeFolder.live

  override val spec = suite("WithHomeFolder")(
    homeFolder,
  )

}
