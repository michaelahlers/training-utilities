package ahlers.training.tools.conversion

import ahlers.training.tools.ToolsApp
import ahlers.training.tools.ToolsApp.DryRun
import ahlers.training.tools.conversion.TrainerRoadWorkoutZwiftWorkoutApp.InputLocation
import ahlers.training.tools.conversion.TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation
import ahlers.trainingutilities.tools.BuildInfo
import zio.Runtime
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.cli.extensions._
import zio.cli.figlet.FigFont
import ahlers.training.tools.ToolsCliApp.DryRunTypeOps
import zio.logging.consoleLogger

object TrainerRoadWorkoutZwiftWorkoutCliApp extends ZIOCliDefault {

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  implicit class InputLocationOps(private val self: InputLocation.type) extends AnyVal {
    def options: Options[InputLocation] =
      (Options.uri("input-uri") ?? """Where to find the TrainerRoad workout for conversion.""")
        .map(InputLocation)
  }

  implicit class OutputLocationOps(private val self: OutputLocation.type) extends AnyVal {
    def options: Options[OutputLocation] =
      (Options.uri("output-uri") ?? """Where to save converted Zwift workout; if not specified, will attempt to guess.""")
        .map(OutputLocation)
  }

  val options: Options[(DryRun, InputLocation, OutputLocation)] =
    DryRun.options ++
      InputLocation.options ++
      OutputLocation.options

  val args: Args[Unit] =
    Args.Empty

  val command: Command[TrainerRoadWorkoutZwiftWorkoutApp] =
    Command("trainer-road-workout:zwift-workout", options, args)
      .withHelp(HelpDoc.p("Converts a TrainerRoad workout into a Zwift workout."))
      .map((TrainerRoadWorkoutZwiftWorkoutApp.apply _).tupled)

  override val cliApp = CliApp.make(
    name = s"""${BuildInfo.name}: TrainerRoad Workout / Zwift Workout""",
    version = BuildInfo.version,
    summary = text("Converts a TrainerRoad workout into a Zwift workout."),
    command = command,
    figFont = FigFont.Default,
  )(_.run)

}
