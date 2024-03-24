package ahlers.training.tools.convert.from.trainerroad.to.zwift

import ahlers.training.conversions.from.trainerroad.to.zwift.ToWorkoutFile
import ahlers.training.tools.ToolsApp
import ahlers.training.tools.convert.from.trainerroad.to.zwift.WorkoutApp.OutputLocation
import java.net.URI
import scala.xml.NodeSeq
import scala.xml.PrettyPrinter
import scala.xml.encoding.syntax.XmlEncoderOps
import trainerroad.schema.web.Details
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

case class WorkoutApp(
  dryRun: ToolsApp.DryRun,
  inputLocation: WorkoutApp.InputLocation,
  outputLocation: Option[WorkoutApp.OutputLocation],
) extends ZIOAppDefault { self =>

  override val bootstrap =
    Runtime.removeDefaultLoggers >>> consoleLogger()

  type From = trainerroad.schema.web.WorkoutDetails
  type To   = zwift.schema.desktop.WorkoutFile

  val read: ZStream[Any, Throwable, From] = {
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

  case class Targeted(encoded: Encoded, outputLocation: OutputLocation)

  val target: ZPipeline[Any, Throwable, Encoded, Targeted] =
    ZPipeline.mapStream[Any, Throwable, Encoded, Targeted] { encoded =>
      import encoded.converted

      val details: Details = converted
        .from
        .workout
        .details

      outputLocation
        .map(outputLocation => ZStream.succeed(Targeted(encoded, outputLocation)))
        .getOrElse {
          ZStream.fromZIO(WithZwiftWorkoutsFolders.zwiftWorkoutsFolders)
            .map(_.toNonEmptyChunk.toChunk)
            .flattenChunks
            .map { workoutFolder =>
              val trainerRoadFolder = workoutFolder / "TrainerRoad"
              val workoutFile = {
                val id = details.id
                val name = details.workoutName
                  .toLowerCase()
                  .replace("+", "plus ")
                  .replace("-", "minus ")
                  .split(' ')
                  .mkString("-")

                trainerRoadFolder / s"$id-$name.zwo"
              }

              val outputLocation = OutputLocation(workoutFile.path.toUri)
              Targeted(encoded, outputLocation)
            }
        }
    }

  val write: ZSink[Any, Throwable, Targeted, Unit, Any] =
    ZSink.foreach[Any, Throwable, Targeted] { targeted =>
      import targeted.encoded

      val buffer = ZStream.fromChunk(encoded.buffer)
      val write  = ZSink.fromFileURI(targeted.outputLocation.toUri)

      for {
        _ <- ZIO.logInfo(s"Performing $dryRun conversion of $inputLocation to ${targeted.outputLocation}.")
        _ <- buffer >>> write
      } yield ()
    }

  override val run =
    read >>> convert >>> encode >>> target >>> write

}

object WorkoutApp {
  case class InputLocation(toUri: URI)
  case class OutputLocation(toUri: URI)
}
