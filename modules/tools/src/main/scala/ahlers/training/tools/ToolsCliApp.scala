package ahlers.training.tools

import ahlers.training.tools.conversion.ConversionToolsTask
import ahlers.trainingutilities.tools.BuildInfo
import java.net.URI
import scala.util.control.NonFatal
import zio.Console.printLine
import zio._
import zio.cli.HelpDoc.Span.text
import zio.cli._

object ToolsCliApp extends ZIOCliDefault {

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout into a different format.")

  val command: Command[ToolsTask] = Command("tools", Options.none, Args.none)
    .subcommands(ConversionToolsTask.command)
    .withHelp(helpDoc)

  override val cliApp = CliApp.make(
    name = BuildInfo.name,
    version = BuildInfo.version,
    summary = text("Tools for working with training data."),
    command = command,
  ) { command =>
    // Implement logic of CliApp
    ZIO.logInfo(s"$command")
    printLine("executing git clone")
  }

}
