package ahlers.training.tools.convert

import scala.xml.encoding.syntax._
import zio.ZIO
import zio.json.JsonDecoder
import zio.json.JsonStreamDelimiter
import zio.stream.ZPipeline

sealed trait Conversion {

  type From
  type To

  def decode: ZPipeline[Any, Throwable, Byte, From]

  def convert: ZPipeline[Any, Throwable, From, To]

  def encode: ZPipeline[Any, Throwable, To, Byte]

  final def total: ZPipeline[Any, Throwable, Byte, Byte] =
    decode >>>
      convert >>>
      encode

}

object Conversion {

  case object FromTrainerRoadWorkoutToZwiftWorkout extends Conversion {

    override type From = TrainerRoadWorkout
    override type To   = ZwiftWorkout

    import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.ToWorkoutFile
    import scala.xml.PrettyPrinter

    override val decode: ZPipeline[Any, Throwable, Byte, From] =
      ZPipeline.utf8Decode >>>
        ZPipeline[String].mapChunks(_.flatMap(_.toCharArray)) >>>
        JsonDecoder[From].decodeJsonPipeline(JsonStreamDelimiter.Newline)

    override val convert: ZPipeline[Any, Throwable, From, To] =
      ZPipeline.mapZIO(ToWorkoutFile
        .from(_)
        .map(ZIO.succeed(_))
        .valueOr(ZIO.fail(_)))

    override val encode: ZPipeline[Any, Throwable, To, Byte] = {
      val printer = new PrettyPrinter(160, 2)

      ZPipeline[To]
        .map(_
          .asXml)
        .map(printer
          .formatNodes(_)
          .getBytes.toSeq)
        .flattenIterables
    }
  }

}
