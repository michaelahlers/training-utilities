package ahlers.training.tools.conversion

import ahlers.training.tools.ToolsApp
import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.ToWorkoutFile
import ahlers.trainingutilities.tools.BuildInfo
import java.net.URI
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import scala.util.Try
import scala.util.control.NonFatal
import scala.xml.NodeSeq
import scala.xml.PrettyPrinter
import scala.xml.encoding.syntax.XmlEncoderOps
import trainerroad.schema.web.WorkoutDetails
import zio.ZIO
import zio.ZIOAppDefault
import zio.cli.Args
import zio.cli.CliApp
import zio.cli.Command
import zio.cli.HelpDoc
import zio.cli.HelpDoc.Span.text
import zio.cli.Options
import zio.cli.ValidationError
import zio.cli.ValidationErrorType
import zio.cli.ZIOCliDefault
import zio.cli.extensions._
import zio.cli.figlet.FigFont
import zio.cli.figlet.FigFontFiles
import zio.json.JsonDecoder
import zio.json.JsonStreamDelimiter
import zio.stream.ZPipeline
import zio.stream.ZSink
import zio.stream.ZStream

object TrainerRoadWorkoutZwiftWorkoutCliApp extends ZIOCliDefault {

  case class InputLocation(toUri: URI)
  object InputLocation {
    val options: Options[InputLocation] =
      (Options.uri("input-uri") ?? """Where to find the TrainerRoad workout for conversion.""")
        .map(InputLocation(_))
  }

  case class OutputLocation(toUri: URI)
  object OutputLocation {
    val options: Options[OutputLocation] =
      (Options.uri("output-uri") ?? """Where to save converted Zwift workout; if not specified, will attempt to guess.""")
        .map(OutputLocation(_))
  }

  val options: Options[(ToolsApp.DryRun, InputLocation, OutputLocation)] =
    ToolsApp.DryRun.options ++
      InputLocation.options ++
      OutputLocation.options

  val args: Args[Unit] =
    Args.Empty

  val command: Command[TrainerRoadWorkoutZwiftWorkoutApp] =
    Command("trainer-road-workout-zwift-workout", options, args)
      .withHelp(HelpDoc.p("Converts a TrainerRoad workout into a Zwift workout."))
      .map((TrainerRoadWorkoutZwiftWorkoutApp.apply _).tupled)

  override val cliApp = CliApp.make(
    name = s"""${BuildInfo.name}: TrainerRoad / Workout""",
    version = BuildInfo.version,
    summary = text("Converts a TrainerRoad workout into a Zwift workout."),
    command = command,
    figFont = FigFont.Default,
  )(_.run)

}

case class TrainerRoadWorkoutZwiftWorkoutApp(
  dryRun: ToolsApp.DryRun,
  inputLocation: TrainerRoadWorkoutZwiftWorkoutCliApp.InputLocation,
  outputLocation: TrainerRoadWorkoutZwiftWorkoutCliApp.OutputLocation,
) extends ZIOAppDefault { self =>

  type From = trainerroad.schema.web.WorkoutDetails
  type To   = zwift.schema.desktop.WorkoutFile

  val input: ZStream[Any, Throwable, Byte] =
    ZStream.fromFileURI(inputLocation.toUri)

  val decode: ZPipeline[Any, Throwable, Byte, From] =
    ZPipeline.utf8Decode >>>
      ZPipeline[String].mapChunks(_.flatMap(_.toCharArray)) >>>
      JsonDecoder[WorkoutDetails].decodeJsonPipeline(JsonStreamDelimiter.Newline)

  val convert: ZPipeline[Any, Throwable, From, To] =
    ZPipeline.mapZIO(ToWorkoutFile
      .from(_)
      .map(ZIO.succeed(_))
      .valueOr(ZIO.fail(_)))

  val encode: ZPipeline[Any, Throwable, To, Byte] = {
    val printer = new PrettyPrinter(160, 2)

    ZPipeline[To].map(_.asXml) >>>
      ZPipeline[NodeSeq].map(printer.formatNodes(_)) >>>
      ZPipeline[String].mapChunks(_.flatMap(_.getBytes))
  }

  val output: ZSink[Any, Throwable, Byte, Byte, Long] =
    ZSink.fromFileURI(outputLocation.toUri)

  val total: ZIO[Any, Throwable, Long] =
    input >>>
      decode >>>
      convert >>>
      encode >>>
      output

  override val run = for {
    _ <- ZIO.logInfo(s"Performing $dryRun conversion of $inputLocation to $outputLocation.")
    _ <- total
  } yield ()

}
