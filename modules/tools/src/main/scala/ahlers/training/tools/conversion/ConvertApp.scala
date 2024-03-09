package ahlers.training.tools.conversion

import zio._
import zio.logging.consoleLogger

/**
 * @todo Maybe parameterize [[conversion]]?
 */
case class ConvertApp(
  conversion: ZIOAppDefault,
) extends ZIOAppDefault { self =>

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  val run = for {
    _ <- ZIO.logInfo(s"Performing $conversion conversion.")
    _ <- conversion.run
  } yield ()

}
