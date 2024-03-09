package ahlers.training.tools.conversion

import ahlers.trainingutilities.tools.BuildInfo
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.cli.figlet.FigFont

object ConvertCliApp extends ZIOCliDefault {

  val options: Options[Unit] = Options.Empty

  val args: Args[Unit] = Args.Empty

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout or activity into a different format.")

  val command: Command[ConvertApp] = Command("convert", options, args)
    .subcommands(TrainerRoadWorkoutZwiftWorkoutCliApp.command)
    .withHelp(helpDoc)
    .map { conversion =>
      ConvertApp(
        conversion = conversion,
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
