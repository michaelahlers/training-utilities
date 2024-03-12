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

  case class Converted(from: From, to: To)

  val convert: ZPipeline[Any, Throwable, From, Converted] =
    ZPipeline.mapZIO(from =>
      ToWorkoutFile
        .from(from)
        .map(to => ZIO.succeed(Converted(from, to)))
        .valueOr(ZIO.fail(_)),
    )

  case class Encoded(converted: Converted, buffer: Chunk[Byte])

  val encode: ZPipeline[Any, Throwable, Converted, Encoded] = {
    val printer = new PrettyPrinter(160, 2)

    ZPipeline.map[Converted, Encoded] { converted =>
      val buffer: Chunk[Byte] = Chunk.fromIterable(printer
        .formatNodes(converted.to.asXml)
        .getBytes)

      Encoded(converted, buffer)
    }
  }

  val to: ZSink[Any, Throwable, Encoded, Unit, Any] = {

    val outputsF: ZIO[Any, Throwable, Chunk[ZSink[Any, Throwable, Byte, Byte, Long]]] = outputLocations
      .map { outputLocation =>
        ZSink.fromFileURI(outputLocation.toUri)
      }
      .runCollect

    ZSink.foreach[Any, Throwable, Encoded] { encoded =>
      for {
        outputs <- outputsF
        _       <- ZIO.foreachPar(outputs)(ZStream.fromIterable(encoded.buffer).run(_))
      } yield ()
    }
  }

  override val run = for {
    _ <- ZIO.logInfo(s"Performing $dryRun conversion of $inputLocation to $outputLocation.")
    _ <- from >>> convert >>> encode >>> to
  } yield ()

}

object TrainerRoadWorkoutZwiftWorkoutApp {
  case class InputLocation(toUri: URI)
  case class OutputLocation(toUri: URI)
}
