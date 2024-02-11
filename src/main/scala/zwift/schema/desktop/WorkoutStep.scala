package zwift.schema.desktop

import scala.xml.Text
import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._
import squants.time.Seconds
import squants.time.Time

sealed trait WorkoutStep {
  def duration: Time
  def ftpRatioStart: Float
  def ftpRatioEnd: Float
}

object WorkoutStep {

  implicit private val xmlEncoderTime: XmlEncoder[Time] = time =>
    Text(time.toSeconds.toInt.toString)

  case class SteadyState(
    duration: Time,
    ftpRatio: Float,
  ) extends WorkoutStep {

    override val ftpRatioStart: Float = ftpRatio
    override val ftpRatioEnd: Float = ftpRatio

    require(
      duration >= Seconds(0),
      s"Duration ($duration) must be non-negative.",
    )

    require(
      ftpRatio >= 0,
      s"FTP power ratio ($ftpRatio) must be non-negative.",
    )

  }

  object SteadyState {
    implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
      <SteadyState
        Duration={step.duration.asXml}
        Power={step.ftpRatio.toString} />
  }

  case class Ramp(
    duration: Time,
    ftpRatioStart: Float,
    ftpRatioEnd: Float,
  ) extends WorkoutStep {

    require(
      ftpRatioStart >= 0,
      s"Low FTP ratio ($ftpRatioStart) must be non-negative.",
    )

    require(
      ftpRatioEnd >= 0,
      s"High FTP ratio ($ftpRatioEnd) must be non-negative.",
    )

  }

  object Ramp {
    implicit val xmlEncoder: XmlEncoder[Ramp] = step =>
      <Ramp
        Duration={step.duration.asXml}
        PowerLow={step.ftpRatioStart.toString}
        PowerHigh={step.ftpRatioEnd.toString} />
  }

  case class Warmup(
    duration: Time,
    ftpRatioStart: Float,
    ftpRatioEnd: Float,
  ) extends WorkoutStep {

    require(
      ftpRatioStart >= 0,
      s"Low FTP ratio ($ftpRatioStart) must be non-negative.",
    )

    require(
      ftpRatioEnd >= 0,
      s"High FTP ratio ($ftpRatioEnd) must be non-negative.",
    )

  }

  object Warmup {
    implicit val xmlEncoder: XmlEncoder[Warmup] = step =>
      <Warmup
        Duration={step.duration.asXml}
        PowerLow={step.ftpRatioStart.toString}
        PowerHigh={step.ftpRatioEnd.toString} />
  }

  case class Cooldown(
    duration: Time,
    ftpRatioStart: Float,
    ftpRatioEnd: Float,
  ) extends WorkoutStep {

    require(
      ftpRatioStart >= 0,
      s"Low FTP ratio ($ftpRatioStart) must be non-negative.",
    )

    require(
      ftpRatioEnd >= 0,
      s"High FTP ratio ($ftpRatioEnd) must be non-negative.",
    )

  }

  object Cooldown {
    implicit val xmlEncoder: XmlEncoder[Cooldown] = step =>
      <Cooldown
        Duration={step.duration.asXml}
        PowerLow={step.ftpRatioStart.toString}
        PowerHigh={step.ftpRatioEnd.toString} />
  }

  implicit val xmlEncoder: XmlEncoder[WorkoutStep] = {
    case step: Warmup => step.asXml
    case step: SteadyState => step.asXml
    case step: Ramp => step.asXml
    case step: Cooldown => step.asXml
  }

}
