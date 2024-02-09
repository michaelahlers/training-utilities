package zwift.schema.desktop

import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._

sealed trait WorkoutStep {
  def durationSeconds: Int
  def ftpRatioLow: Float
  def ftpRatioHigh: Float
}

object WorkoutStep {

  case class SteadyState(
    durationSeconds: Int,
    ftpRatio: Float,
  ) extends WorkoutStep {

    override val ftpRatioLow: Float = ftpRatio
    override val ftpRatioHigh: Float = ftpRatio

    require(
      durationSeconds >= 0,
      s"Duration ($durationSeconds) must be non-negative.",
    )

    require(
      ftpRatio >= 0,
      s"FTP power ratio ($ftpRatio) must be non-negative.",
    )

  }

  object SteadyState {
    implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
      <SteadyState
        Duration={step.durationSeconds.toString}
        Power={step.ftpRatio.toString} />
  }

  case class Ramp(
    durationSeconds: Int,
    ftpRatioLow: Float,
    ftpRatioHigh: Float,
  ) extends WorkoutStep {

    require(
      ftpRatioLow >= 0,
      s"Low FTP ratio ($ftpRatioLow) must be non-negative.",
    )

    require(
      ftpRatioHigh >= 0,
      s"High FTP ratio ($ftpRatioHigh) must be non-negative.",
    )

  }

  object Ramp {
    implicit val xmlEncoder: XmlEncoder[Ramp] = step =>
      <Ramp
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpRatioLow.toString}
        PowerHigh={step.ftpRatioHigh.toString} />
  }

  case class Warmup(
    durationSeconds: Int,
    ftpRatioLow: Float,
    ftpRatioHigh: Float,
  ) extends WorkoutStep {

    require(
      ftpRatioLow >= 0,
      s"Low FTP ratio ($ftpRatioLow) must be non-negative.",
    )

    require(
      ftpRatioHigh >= 0,
      s"High FTP ratio ($ftpRatioHigh) must be non-negative.",
    )

  }

  object Warmup {
    implicit val xmlEncoder: XmlEncoder[Warmup] = step =>
      <Warmup
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpRatioLow.toString}
        PowerHigh={step.ftpRatioHigh.toString} />
  }

  case class Cooldown(
    durationSeconds: Int,
    ftpRatioLow: Float,
    ftpRatioHigh: Float,
  ) extends WorkoutStep {

    require(
      ftpRatioLow >= 0,
      s"Low FTP ratio ($ftpRatioLow) must be non-negative.",
    )

    require(
      ftpRatioHigh >= 0,
      s"High FTP ratio ($ftpRatioHigh) must be non-negative.",
    )

  }

  object Cooldown {
    implicit val xmlEncoder: XmlEncoder[Cooldown] = step =>
      <Cooldown
        Duration={step.durationSeconds.toString}
        PowerLow={step.ftpRatioLow.toString}
        PowerHigh={step.ftpRatioHigh.toString} />
  }

  implicit val xmlEncoder: XmlEncoder[WorkoutStep] = {
    case step: Warmup => step.asXml
    case step: SteadyState => step.asXml
    case step: Ramp => step.asXml
    case step: Cooldown => step.asXml
  }

}
