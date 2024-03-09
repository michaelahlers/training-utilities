package ahlers.training.tools

import ahlers.training.tools.convert.ConvertApp
import ahlers.training.tools.convert.ConvertCliApp
import ahlers.trainingutilities.tools.BuildInfo
import zio._
import zio.cli.HelpDoc.Span.text
import zio.cli._
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
