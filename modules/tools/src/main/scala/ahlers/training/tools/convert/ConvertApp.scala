package ahlers.training.tools.convert

import ahlers.training.tools.convert.from.trainerroad.to.zwift.WorkoutApp
import zio._
import zio.logging.consoleLogger

sealed trait ConvertApp extends ZIOAppDefault {

  override val bootstrap =
    Runtime.removeDefaultLoggers >>>
      consoleLogger()

}

object ConvertApp {

  case class FromTrainerRoadWorkoutToZwiftWorkout(
    delegate: WorkoutApp,
  ) extends ConvertApp {

    override val run = for {
      _ <- ZIO.logInfo(s"Running tool $delegate.")
      _ <- delegate.run
    } yield ()

  }

  def apply(
    delegate: WorkoutApp,
  ): ConvertApp = FromTrainerRoadWorkoutToZwiftWorkout(delegate)

}
