package zwift.schema.desktop

import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._

sealed trait WorkoutStep
object WorkoutStep {

  case class SteadyState(
    durationSeconds: Int,
    ftpPowerRatio: Float,
  ) extends WorkoutStep {

    require(
      durationSeconds >= 0,
      s"Duration ($durationSeconds) must be non-negative.",
    )

    require(
      ftpPowerRatio >= 0,
      s"FTP power ratio ($ftpPowerRatio) must be non-negative.",
    )

  }

  object SteadyState {
    implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
      <SteadyState
        Duration={step.durationSeconds.toString}
        Power={step.ftpPowerRatio.toString} />
  }

  case class Ramp(
    durationSeconds: Int,
    ftpPowerLowRatio: Float,
    ftpPowerHighRatio: Float,
  ) extends WorkoutStep {

    require(
      ftpPowerLowRatio >= 0,
      s"FTP power low ratio ($ftpPowerLowRatio) must be non-negative.",
    )

    require(
      ftpPowerLowRatio <= ftpPowerHighRatio,
      s"FTP power low ratio ($ftpPowerLowRatio) must be less than or equal to FTP power high ratio ($ftpPowerHighRatio).",
    )

  }

  object Ramp {
    implicit val xmlEncoder: XmlEncoder[Ramp] = step =>
      <Ramp
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpPowerLowRatio.toString}
        PowerHigh={step.ftpPowerHighRatio.toString} />
  }

  implicit val xmlEncoder: XmlEncoder[WorkoutStep] = {
    case step: SteadyState => step.asXml
  }

}
