package ahlers.training.tools.conversion

import scala.xml.encoding.syntax._
import zio.ZIO
import zio.json.JsonDecoder
import zio.stream.ZPipeline

sealed trait Conversion[A, B] {

  def decode: ZPipeline[Any, Throwable, Char, A]

  def convert: ZPipeline[Any, Throwable, A, B]

  def encode: ZPipeline[Any, Throwable, B, Char]

}

object Conversion {

  case object FromTrainerRoadWorkoutToZwiftWorkout extends Conversion[TrainerRoadWorkout, ZwiftWorkout] {

    import ahlers.trainingutilities.conversion.fromTrainerRoad.toZwift.ToWorkoutFile
    import scala.xml.PrettyPrinter

    override val decode: ZPipeline[Any, Throwable, Char, TrainerRoadWorkout] =
      JsonDecoder[TrainerRoadWorkout].decodeJsonPipeline()

    override val convert: ZPipeline[Any, Throwable, TrainerRoadWorkout, ZwiftWorkout] =
      ZPipeline.mapZIO(ToWorkoutFile
        .from(_)
        .map(ZIO.succeed(_))
        .valueOr(ZIO.fail(_)))

    override val encode: ZPipeline[Any, Throwable, ZwiftWorkout, Char] = {
      val printer = new PrettyPrinter(160, 2)

      ZPipeline[ZwiftWorkout]
        .map(_
          .asXml)
        .map(printer
          .formatNodes(_)
          .toCharArray.toSeq)
        .flattenIterables
    }
  }

}
