package ahlers.training.tools.convert

import zio._
import zio.logging.consoleLogger

/**
 * @todo Maybe parameterize [[delegate]]?
 */
case class ConvertApp(
  delegate: ZIOAppDefault,
) extends ZIOAppDefault { self =>

  val run = for {
    _ <- ZIO.logInfo(s"Performing $delegate conversion.")
    _ <- delegate.run
  } yield ()

}
