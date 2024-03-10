package ahlers.training.tools.convert.vendor

import ahlers.training.tools.ToolsApp
import ahlers.training.tools.ToolsApp.DryRun
import ahlers.training.tools.ToolsCliApp.DryRunTypeOps
import ahlers.trainingutilities.tools.BuildInfo
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

  }

  case class WithSettings(
    settings: Settings,
  )

  object WithSettings {

    val settings: ZIO[Any, Throwable, Settings] = ZIO.attempt(ConfigFactory
      .parseURL(Resource.my.getUrl("TrainerRoadWorkoutZwiftWorkoutCliApp.conf"))
      .resolve())
      .flatMap(ConfigProvider
        .fromTypesafeConfig(_)
        .load(deriveConfig[Settings]))

    val live: ZLayer[Any, Throwable, WithSettings] = ZLayer.fromZIO {
      settings.map(WithSettings(_))
    }
  }

  case class WithDocumentsFolder(
    documentsFolder: File,
  )

  object WithDocumentsFolder {

    val homeFolder: ZIO[Any, Throwable, File] = ZIO
      .attempt {

        /**
         * If the `OneDrive` environment variable is set, then running on Windows and the logged in user (and, by extension, Zwift) stores documents there.
         */
        sys.props.get("OneDrive") match {
          case None           => File.home
          case Some(oneDrive) => File(oneDrive)
        }
      }
      .tap { folder =>
        if (folder.exists && folder.isDirectory) ZIO.unit
        else ZIO.fail(new IllegalStateException(s"""Home folder "$folder" does not exist."""))
      }

    val documentsFolder: ZIO[Any, Throwable, File] = homeFolder
      .map(_ / "Documents")
      .tap { folder =>
        if (folder.exists && folder.isDirectory) ZIO.unit
        else ZIO.fail(new IllegalStateException(s"""Documents folder "$folder" does not exist."""))
      }

    val live: ZLayer[Any, Throwable, WithDocumentsFolder] = ZLayer.fromZIO {
      documentsFolder.map(WithDocumentsFolder(_))
    }
  }

  override val bootstrap =
    (Runtime.removeDefaultLoggers >>> consoleLogger()) >>>
      WithSettings.live

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
      _ <- command.run
    } yield ()
  }

}
