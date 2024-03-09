package ahlers.training.tools.conversion

import zio._
import zio.logging.consoleLogger

/**
 * @todo Maybe parameterize [[delegate]]?
 */
case class ConvertApp(
  delegate: ZIOAppDefault,
) extends ZIOAppDefault { self =>

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  val run = for {
    _ <- ZIO.logInfo(s"Performing $delegate conversion.")
    _ <- delegate.run
  } yield ()

}
