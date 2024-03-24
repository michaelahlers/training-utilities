package ahlers.training.tools.convert.from.trainerroad.to.zwift

import ahlers.training.tools.ToolsApp
import ahlers.training.tools.ToolsApp.DryRun
import ahlers.training.tools.ToolsCliApp.DryRunTypeOps
import ahlers.training.tools.WithHomeFolder
import ahlers.training.tools.BuildInfo
import better.files.File
import better.files.Resource
import com.typesafe.config.ConfigFactory
import zio.ConfigProvider
import zio.Runtime
import zio.ZIO
import zio.ZLayer
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.cli.extensions._
import zio.cli.figlet.FigFont
import zio.config.magnolia.deriveConfig
import zio.config.typesafe._
import zio.logging.consoleLogger

object WorkoutCliApp extends ZIOCliDefault {

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

  }

  case class WithSettings(
    settings: Settings,
  )

  object WithSettings {

    val settings: ZIO[Any, Throwable, Settings] = ZIO.attempt(ConfigFactory
      .parseURL(Resource.my.getUrl("WorkoutCliApp.conf"))
      .resolve())
      .flatMap(ConfigProvider
        .fromTypesafeConfig(_)
        .load(deriveConfig[Settings]))

    val live: ZLayer[Any, Throwable, WithSettings] = ZLayer.fromZIO {
      settings.map(WithSettings(_))
    }

  }

  override val bootstrap =
    (Runtime.removeDefaultLoggers >>> consoleLogger()) >>>
      WithHomeFolder.live >>>
      WithSettings.live

  implicit class InputLocationOps(private val self: WorkoutApp.InputLocation.type) extends AnyVal {
    def options: Options[WorkoutApp.InputLocation] =
      (Options.uri("input-uri") ?? """Where to find the TrainerRoad workout for conversion.""")
        .map(WorkoutApp.InputLocation)
  }

  implicit class OutputLocationOps(private val self: WorkoutApp.OutputLocation.type) extends AnyVal {
    def options: Options[WorkoutApp.OutputLocation] =
      (Options.uri("output-uri") ?? """Where to save converted Zwift workout; if not specified, will attempt to guess.""")
        .map(WorkoutApp.OutputLocation)
  }

  val options: Options[(DryRun, WorkoutApp.InputLocation, Option[WorkoutApp.OutputLocation])] =
    ToolsApp.DryRun.options ++
      WorkoutApp.InputLocation.options ++
      WorkoutApp.OutputLocation.options.optional

  val args: Args[Unit] =
    Args.Empty

  def command: Command[WorkoutApp] =
    Command("trainer-road-workout:zwift-workout", options, args)
      .withHelp(HelpDoc.p("Converts a TrainerRoad workout into a Zwift workout."))
      .map((WorkoutApp.apply _).tupled)

  override val cliApp = CliApp.make(
    name = s"""${BuildInfo.name}: TrainerRoad Workout / Zwift Workout""",
    version = BuildInfo.version,
    summary = text("Converts a TrainerRoad workout into a Zwift workout."),
    command = command,
    figFont = FigFont.Default,
  ) { command =>
    for {
      _ <- command.run
    } yield ()
  }

}
