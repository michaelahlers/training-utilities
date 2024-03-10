package ahlers.training.tools.convert.vendor

import ahlers.training.tools.ToolsApp
import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.ToWorkoutFile
import java.net.URI
import scala.xml.NodeSeq
import scala.xml.PrettyPrinter
import scala.xml.encoding.syntax.XmlEncoderOps
import trainerroad.schema.web.WorkoutDetails
import zio.ZIO
import zio.ZIOAppDefault
import zio.json.JsonDecoder
import zio.json.JsonStreamDelimiter
import zio.stream.ZPipeline
import zio.stream.ZSink
import zio.stream.ZStream

case class TrainerRoadWorkoutZwiftWorkoutApp(
  dryRun: ToolsApp.DryRun,
  inputLocation: TrainerRoadWorkoutZwiftWorkoutApp.InputLocation,
  outputLocation: TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation,
) extends ZIOAppDefault { self =>

  case class WithParameters(
    dryRun: ToolsApp.DryRun,
    inputLocation: TrainerRoadWorkoutZwiftWorkoutApp.InputLocation,
    outputLocation: TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation,
  )

  override type Environment = WithParameters

  type From = trainerroad.schema.web.WorkoutDetails
  type To   = zwift.schema.desktop.WorkoutFile

  val input: ZStream[Environment, Throwable, Byte] =
    ZStream.fromFileURI(inputLocation.toUri)

  val decode: ZPipeline[Environment, Throwable, Byte, From] =
    ZPipeline.utf8Decode >>>
      ZPipeline[String].mapChunks(_.flatMap(_.toCharArray)) >>>
      JsonDecoder[WorkoutDetails].decodeJsonPipeline(JsonStreamDelimiter.Newline)

  val convert: ZPipeline[Environment, Throwable, From, To] =
    ZPipeline.mapZIO(ToWorkoutFile
      .from(_)
      .map(ZIO.succeed(_))
      .valueOr(ZIO.fail(_)))

  val encode: ZPipeline[Environment, Throwable, To, Byte] = {
    val printer = new PrettyPrinter(160, 2)

    ZPipeline[To].map(_.asXml) >>>
      ZPipeline[NodeSeq].map(printer.formatNodes(_)) >>>
      ZPipeline[String].mapChunks(_.flatMap(_.getBytes))
  }

  val output: ZSink[Environment, Throwable, Byte, Byte, Long] =
    ZSink.fromFileURI(outputLocation.toUri)

  val total: ZIO[Environment, Throwable, Long] =
    input >>>
      decode >>>
      convert >>>
      encode >>>
      output

  override val run: ZIO[Environment, Throwable, Unit] = for {
    _ <- ZIO.logInfo(s"Performing $dryRun conversion of $inputLocation to $outputLocation.")
    _ <- total
  } yield ()

}

object TrainerRoadWorkoutZwiftWorkoutApp {
  case class InputLocation(toUri: URI)
  case class OutputLocation(toUri: URI)
}
