package ahlers.training.tools

import ahlers.training.tools.BuildInfo
import ahlers.training.tools.ToolsApp.DryRun
import ahlers.training.tools.convert.ConvertCliApp
import zio._
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.logging.consoleLogger

object ToolsCliApp extends ZIOCliDefault {

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  implicit class DryRunTypeOps(private val self: DryRun.type) {
    def options: Options[DryRun] = Options
      .boolean("dry-run").alias("n")
      .map {
        case true  => DryRun.IsDry
        case false => DryRun.IsWet
      }
  }

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout into a different format.")

  val command: Command[ToolsApp] = Command("tools", Options.none, Args.none)
    .subcommands(ConvertCliApp.command.map(ToolsApp(_)))
    .withHelp(helpDoc)

  override val cliApp = CliApp.make(
    name = BuildInfo.name,
    version = BuildInfo.version,
    summary = text("Tools for working with training data."),
    command = command,
  )(_.run)

}
