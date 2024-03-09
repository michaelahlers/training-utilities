package ahlers.training.tools.conversion

import zio._

/**
 * @todo Maybe parameterize [[conversion]]?
 */
case class ConvertApp(
  conversion: ZIOAppDefault,
) extends ZIOAppDefault { self =>

  val run = for {
    _ <- ZIO.logInfo(s"Performing $conversion conversion.")
    _ <- conversion.run
  } yield ()

}
