package ahlers.training.tools.conversion

import zio._

case class ConversionApp(
  toolsTask: ConversionToolsTask,
) extends ZIOAppDefault {
  val run = for {
    _ <- ZIO.logInfo(s"ConversionApp created with $toolsTask.")
  } yield ()
}
