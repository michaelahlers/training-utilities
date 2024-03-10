package ahlers.training.tools.convert.vendor

import ahlers.training.tools.ToolsApp
import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation
import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.ToWorkoutFile
import java.net.URI
import scala.xml.NodeSeq
import scala.xml.PrettyPrinter
import scala.xml.encoding.syntax.XmlEncoderOps
import trainerroad.schema.web.WorkoutDetails
import zio.Chunk
import zio.Hub
import zio.Queue
import zio.Runtime
import zio.ZIO
import zio.ZIOAppDefault
import zio.json.JsonDecoder
import zio.json.JsonStreamDelimiter
import zio.logging.consoleLogger
import zio.prelude.NonEmptyList
import zio.stream.ZChannel
import zio.stream.ZPipeline
import zio.stream.ZSink
import zio.stream.ZStream
import zwift.desktop.WithZwiftWorkoutsFolders

case class TrainerRoadWorkoutZwiftWorkoutApp(
  dryRun: ToolsApp.DryRun,
  inputLocation: TrainerRoadWorkoutZwiftWorkoutApp.InputLocation,
  outputLocation: Option[TrainerRoadWorkoutZwiftWorkoutApp.OutputLocation],
) extends ZIOAppDefault { self =>

  val outputLocations: ZStream[Any, Throwable, OutputLocation] = outputLocation
    .map(ZStream.succeed(_))
    .getOrElse {
      ZStream.fromZIO(WithZwiftWorkoutsFolders.zwiftWorkoutsFolders)
        .map(_.toNonEmptyChunk.toChunk)
        .flattenChunks
        .mapZIO { workoutFolder =>
          val trainerRoadFolder = workoutFolder / "TrainerRoad"

          /** @todo Don't eagerly initialize directories. */
          ZIO.attempt(trainerRoadFolder.createIfNotExists(
            asDirectory = true,
            createParents = false,
          ).path.toUri)
        }
        .map(OutputLocation)
    }

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

  val to: ZSink[Any, Throwable, To, Unit, Any] = {

    val encode: ZPipeline[Any, Throwable, To, Byte] = {
      val printer = new PrettyPrinter(160, 2)
      ZPipeline[To].map(_.asXml) >>>
        ZPipeline[NodeSeq].map(printer.formatNodes(_)) >>>
        ZPipeline[String].mapChunks(_.flatMap(_.getBytes))
    }

    val outputsF: ZIO[Any, Throwable, Chunk[ZSink[Any, Throwable, Byte, Byte, Long]]] = outputLocations
      .map { outputLocation =>
        ZSink.fromFileURI(outputLocation.toUri)
      }
      .runCollect

    ZSink.foreach[Any, Throwable, To] { to =>
      val encodedF: ZIO[Any, Throwable, Seq[Byte]] =
        ZStream.succeed(to).via(encode).runCollect

      for {
        encoded <- encodedF
        outputs <- outputsF
        _       <- ZIO.foreachPar(outputs)(ZStream.fromIterable(encoded).run(_))
      } yield ()
    }
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
