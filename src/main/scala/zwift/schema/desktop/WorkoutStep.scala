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
      ftpPowerHighRatio >= 0,
      s"FTP power low ratio ($ftpPowerHighRatio) must be non-negative.",
    )

  }

  object Ramp {
    implicit val xmlEncoder: XmlEncoder[Ramp] = step =>
      <Ramp
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpPowerLowRatio.toString}
        PowerHigh={step.ftpPowerHighRatio.toString} />
  }

  case class Warmup(
    durationSeconds: Int,
    ftpPowerLowRatio: Float,
    ftpPowerHighRatio: Float,
  ) extends WorkoutStep {

    require(
      ftpPowerLowRatio >= 0,
      s"FTP power low ratio ($ftpPowerLowRatio) must be non-negative.",
    )

    require(
      ftpPowerHighRatio >= 0,
      s"FTP power high ratio ($ftpPowerHighRatio) must be non-negative.",
    )

  }

  object Warmup {
    implicit val xmlEncoder: XmlEncoder[Warmup] = step =>
      <Warmup
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpPowerLowRatio.toString}
        PowerHigh={step.ftpPowerHighRatio.toString} />
  }

  case class Cooldown(
    durationSeconds: Int,
    ftpPowerLowRatio: Float,
    ftpPowerHighRatio: Float,
  ) extends WorkoutStep {

    require(
      ftpPowerLowRatio >= 0,
      s"FTP power low ratio ($ftpPowerLowRatio) must be non-negative.",
    )

    require(
      ftpPowerHighRatio >= 0,
      s"FTP power high ratio ($ftpPowerHighRatio) must be non-negative.",
    )

  }

  object Cooldown {
    implicit val xmlEncoder: XmlEncoder[Cooldown] = step =>
      <Cooldown
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpPowerLowRatio.toString}
        PowerHigh={step.ftpPowerHighRatio.toString} />
  }

  implicit val xmlEncoder: XmlEncoder[WorkoutStep] = {
    case step: Warmup => step.asXml
    case step: SteadyState => step.asXml
    case step: Ramp => step.asXml
    case step: Cooldown => step.asXml
  }

}
