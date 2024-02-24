package ahlers.training.tools

import ahlers.training.tools.conversion.ConvertApp
import ahlers.trainingutilities.tools.BuildInfo
import zio._
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.logging.consoleLogger

object ToolsApp extends ZIOCliDefault {

  sealed trait DryRun
  object DryRun {
    case object IsDry extends DryRun
    case object IsWet extends DryRun

    val options: Options[DryRun] = Options
      .boolean("dry-run").alias("n")
      .map {
        case true  => IsDry
        case false => IsWet
      }

  }

  override val bootstrap =
    Runtime.removeDefaultLoggers >>> consoleLogger()

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout into a different format.")

  val command: Command[ZIOApp] = Command("tools", Options.none, Args.none)
    .subcommands(ConvertApp.command)
    .withHelp(helpDoc)

  override val cliApp = CliApp.make(
    name = BuildInfo.name,
    version = BuildInfo.version,
    summary = text("Tools for working with training data."),
    command = command,
  ) {
    case app: ConvertApp =>
      for {
        _ <- ZIO.logDebug(s"$app")
        _ <- app.run
      } yield ()
  }

}
