package ahlers.training.tools.convert

import ahlers.training.tools.convert.vendor.TrainerRoadWorkoutZwiftWorkoutApp
import zio._
import zio.logging.consoleLogger

sealed trait ConvertApp extends ZIOAppDefault {

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

}

object ConvertApp {

  case class FromTrainerRoadWorkoutToZwiftWorkout(
    delegate: TrainerRoadWorkoutZwiftWorkoutApp,
  ) extends ConvertApp {

    override val run = for {
      _ <- ZIO.logInfo(s"Running tool $delegate.")
      _ <- delegate.run
    } yield ()

  }

  def apply(
    delegate: TrainerRoadWorkoutZwiftWorkoutApp,
  ): ConvertApp = FromTrainerRoadWorkoutToZwiftWorkout(delegate)

}
