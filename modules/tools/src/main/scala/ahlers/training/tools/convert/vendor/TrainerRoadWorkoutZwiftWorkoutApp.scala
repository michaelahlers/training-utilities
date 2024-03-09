package ahlers.training.tools.convert.vendor

import ahlers.training.tools.ToolsApp
import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.ToWorkoutFile
import java.net.URI
import scala.xml.NodeSeq
import scala.xml.PrettyPrinter
import scala.xml.encoding.syntax.XmlEncoderOps
import trainerroad.schema.web.WorkoutDetails
import zio.Runtime
import zio.ZIO
import zio.ZIOAppDefault
import zio.json.JsonDecoder
import zio.json.JsonStreamDelimiter
import zio.logging.consoleLogger
import zio.stream.ZPipeline
import zio.stream.ZSink
import zio.stream.ZStream

case class TrainerRoadWorkoutZwiftWorkoutApp(
  settings: TrainerRoadWorkoutZwiftWorkoutApp.Settings,
  dryRun: ToolsApp.DryRun,
  inputLocation: TrainerRoadWorkoutZwiftWorkoutApp.InputLocation,
  outputLocation: TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation,
) extends ZIOAppDefault { self =>

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

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

object TrainerRoadWorkoutZwiftWorkoutApp {

  import better.files.Resource
  import com.typesafe.config.ConfigFactory
  import zio._
  import zio.config.magnolia._
  import zio.config.typesafe._

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

  case class InputLocation(toUri: URI)
  case class OutputLocation(toUri: URI)

}
