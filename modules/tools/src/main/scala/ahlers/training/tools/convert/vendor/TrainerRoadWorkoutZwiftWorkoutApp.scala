package ahlers.training.tools.convert.vendor

import ahlers.training.tools.ToolsApp
import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation
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
import zio.prelude.NonEmptyList
import zio.stream.ZPipeline
import zio.stream.ZSink
import zio.stream.ZStream
import zwift.desktop.WithZwiftWorkoutsFolders

case class TrainerRoadWorkoutZwiftWorkoutApp(
  dryRun: ToolsApp.DryRun,
  inputLocation: TrainerRoadWorkoutZwiftWorkoutApp.InputLocation,
  outputLocation: Option[TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation],
) extends ZIOAppDefault { self =>

  override val bootstrap =
    Runtime.removeDefaultLoggers >>> consoleLogger()

  type From = trainerroad.schema.web.WorkoutDetails
  type To   = zwift.schema.desktop.WorkoutFile

  val from: ZStream[Any, Throwable, From] = {
    val input: ZStream[Any, Throwable, Byte] =
      ZStream.fromFileURI(inputLocation.toUri)

    val decode: ZPipeline[Any, Throwable, Byte, From] =
      ZPipeline.utf8Decode >>>
        ZPipeline[String].mapChunks(_.flatMap(_.toCharArray)) >>>
        JsonDecoder[WorkoutDetails].decodeJsonPipeline(JsonStreamDelimiter.Newline)

    input >>> decode
  }

  val convert: ZPipeline[Any, Throwable, From, To] =
    ZPipeline.mapZIO(ToWorkoutFile
      .from(_)
      .map(ZIO.succeed(_))
      .valueOr(ZIO.fail(_)))

  val to: ZSink[Any, Throwable, To, Byte, Long] = {

    val encode: ZPipeline[Any, Throwable, To, Byte] = {
      val printer = new PrettyPrinter(160, 2)

      ZPipeline[To].map(_.asXml) >>>
        ZPipeline[NodeSeq].map(printer.formatNodes(_)) >>>
        ZPipeline[String].mapChunks(_.flatMap(_.getBytes))
    }

    def outputFor(outputLocation: OutputLocation): ZSink[Any, Throwable, Byte, Byte, Long] =
      ZSink.fromFileURI(outputLocation.toUri)

    val output: ZSink[Any, Throwable, Byte, Byte, Long] = outputLocation
      .map(outputFor)
      .getOrElse {
        ???
      }

    encode >>> output
  }

  override val run = for {
    _ <- ZIO.logInfo(s"Performing $dryRun conversion of $inputLocation to $outputLocation.")
    _ <- from >>> convert >>> to
  } yield ()

}

object TrainerRoadWorkoutZwiftWorkoutApp {
  case class InputLocation(toUri: URI)
  case class OutputLocation(toUri: URI)
}
