package ahlers.training.tools

import zio._
import zio.logging.consoleLogger

/**
 * @todo Maybe parameterize [[delegate]]?
 */
case class ToolsApp(
  delegate: ZIOAppDefault,
) extends ZIOAppDefault { self =>

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  val run = for {
    _ <- ZIO.logInfo(s"Running tool $delegate.")
    _ <- delegate.run
  } yield ()

}

object ToolsApp {

  sealed trait DryRun
  object DryRun {
    case object IsDry extends DryRun
    case object IsWet extends DryRun
  }

}
