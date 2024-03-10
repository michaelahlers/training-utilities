package ahlers.training.tools

import ahlers.training.tools.convert.ConvertApp
import zio._
import zio.logging.consoleLogger

sealed trait ToolsApp extends ZIOAppDefault {
  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()
}

object ToolsApp {

  sealed trait DryRun
  object DryRun {
    case object IsDry extends DryRun
    case object IsWet extends DryRun
  }

  case class DoConversion(
    delegate: ConvertApp,
  ) extends ToolsApp {

    val run = for {
      _ <- ZIO.logInfo(s"Running tool $delegate.")
      _ <- delegate.run
    } yield ()
  }

  def apply(
    delegate: ConvertApp,
  ): ToolsApp = DoConversion(delegate)

}
