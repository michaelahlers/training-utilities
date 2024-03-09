package ahlers.training.tools.convert

import ahlers.trainingutilities.tools.BuildInfo
import zio.Runtime
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.cli.figlet.FigFont
import zio.logging.consoleLogger

object ConvertCliApp extends ZIOCliDefault {

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  val options: Options[Unit] = Options.Empty

  val args: Args[Unit] = Args.Empty

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout or activity into a different format.")

  val command: Command[ConvertApp] = Command("convert", options, args)
    .subcommands(TrainerRoadWorkoutZwiftWorkoutCliApp.command)
    .withHelp(helpDoc)
    .map { delegate =>
      ConvertApp(
        delegate = delegate,
      )
    }

  override val cliApp = CliApp.make(
    name = s"""${BuildInfo.name}: Convert""",
    version = BuildInfo.version,
    summary = text("Translate from one format to another."),
    command = command,
    figFont = FigFont.Default,
  )(_.run)

}
