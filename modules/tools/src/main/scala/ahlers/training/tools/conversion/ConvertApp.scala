package ahlers.training.tools.conversion

import zio._

case class ConvertApp(
  conversion: ZIOApp,
) extends ZIOAppDefault { self =>

  val run = for {
    _ <- ZIO.logInfo(s"Performing $conversion conversion.")
    _ <- conversion.run
  } yield ()

}
