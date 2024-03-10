package ahlers.training.tools.convert.vendor

import ahlers.training.tools.ToolsApp
import ahlers.training.tools.ToolsApp.DryRun
import ahlers.training.tools.ToolsCliApp.DryRunTypeOps
import ahlers.trainingutilities.tools.BuildInfo
import better.files.Resource
import com.typesafe.config.ConfigFactory
import zio.ConfigProvider
import zio.Runtime
import zio.ZIO
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.cli.extensions._
import zio.cli.figlet.FigFont
import zio.config.magnolia.deriveConfig
import zio.config.typesafe._
import zio.logging.consoleLogger

object TrainerRoadWorkoutZwiftWorkoutCliApp extends ZIOCliDefault {

  case class Settings(
    environment: Settings.Environment,
  )

  object Settings {

    case class Environment(
      home: String,
      windows: Environment.Windows,
    )

    object Environment {

      case class Windows(
        oneDrive: Option[String],
      )

    }

    val load: ZIO[Any, Throwable, Settings] =
      ZIO.attempt(ConfigFactory
        .parseURL(Resource.my.getUrl("TrainerRoadWorkoutZwiftWorkoutApp.conf"))
        .resolve())
        .flatMap(ConfigProvider
          .fromTypesafeConfig(_)
          .load(deriveConfig[Settings]))

  }

  case class WithSettings(
    settings: Settings,
  )

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

  implicit class InputLocationOps(private val self: TrainerRoadWorkoutZwiftWorkoutApp.InputLocation.type) extends AnyVal {
    def options: Options[TrainerRoadWorkoutZwiftWorkoutApp.InputLocation] =
      (Options.uri("input-uri") ?? """Where to find the TrainerRoad workout for conversion.""")
        .map(TrainerRoadWorkoutZwiftWorkoutApp.InputLocation)
  }

  implicit class OutputLocationOps(private val self: TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation.type) extends AnyVal {
    def options: Options[TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation] =
      (Options.uri("output-uri") ?? """Where to save converted Zwift workout; if not specified, will attempt to guess.""")
        .map(TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation)
  }

  val options: Options[(DryRun, TrainerRoadWorkoutZwiftWorkoutApp.InputLocation, TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation)] =
    ToolsApp.DryRun.options ++
      TrainerRoadWorkoutZwiftWorkoutApp.InputLocation.options ++
      TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation.options

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
  ) { command =>
    for {
      _ <- Settings.load
      _ <- command.run
    } yield ()
  }

}
