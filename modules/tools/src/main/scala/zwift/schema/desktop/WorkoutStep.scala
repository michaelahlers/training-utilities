package zwift.schema.desktop

import scala.xml.Text
import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._
import squants.time.Seconds
import squants.time.Time

sealed trait WorkoutStep {
  def duration: Time
  def ftpPercentStart: Float
  def ftpPercentEnd: Float
}

object WorkoutStep {

  implicit private val xmlEncoderTime: XmlEncoder[Time] = time =>
    Text(time.toSeconds.toInt.toString)

  case class SteadyState(
    duration: Time,
    ftpPercent: Float,
  ) extends WorkoutStep {

    override val ftpPercentStart: Float = ftpPercent
    override val ftpPercentEnd: Float = ftpPercent

    require(
      duration >= Seconds(0),
      s"Duration ($duration) must be non-negative.",
    )

    require(
      ftpPercent >= 0,
      s"FTP power percent ($ftpPercent) must be non-negative.",
    )

  }

  object SteadyState {
    implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
      <SteadyState
        Duration={step.duration.asXml}
        Power={(step.ftpPercent / 100f).toString} />
  }

  case class Ramp(
    duration: Time,
    ftpPercentStart: Float,
    ftpPercentEnd: Float,
  ) extends WorkoutStep {

    require(
      ftpPercentStart >= 0,
      s"Low FTP percent ($ftpPercentStart) must be non-negative.",
    )

    require(
      ftpPercentEnd >= 0,
      s"High FTP percent ($ftpPercentEnd) must be non-negative.",
    )

  }

  object Ramp {
    implicit val xmlEncoder: XmlEncoder[Ramp] = step =>
      <Ramp
        Duration={step.duration.asXml}
        PowerLow={(step.ftpPercentStart / 100f).toString}
        PowerHigh={(step.ftpPercentEnd / 100f).toString} />
  }

  case class Warmup(
    duration: Time,
    ftpPercentStart: Float,
    ftpPercentEnd: Float,
  ) extends WorkoutStep {

    require(
      ftpPercentStart >= 0,
      s"Low FTP percent ($ftpPercentStart) must be non-negative.",
    )

    require(
      ftpPercentEnd >= 0,
      s"High FTP percent ($ftpPercentEnd) must be non-negative.",
    )

  }

  object Warmup {
    implicit val xmlEncoder: XmlEncoder[Warmup] = step =>
      <Warmup
        Duration={step.duration.asXml}
        PowerLow={(step.ftpPercentStart / 100f).toString}
        PowerHigh={(step.ftpPercentEnd / 100f).toString} />
  }

  case class Cooldown(
    duration: Time,
    ftpPercentStart: Float,
    ftpPercentEnd: Float,
  ) extends WorkoutStep {

    require(
      ftpPercentStart >= 0,
      s"Low FTP percent ($ftpPercentStart) must be non-negative.",
    )

    require(
      ftpPercentEnd >= 0,
      s"High FTP percent ($ftpPercentEnd) must be non-negative.",
    )

  }

  object Cooldown {
    implicit val xmlEncoder: XmlEncoder[Cooldown] = step =>
      <Cooldown
        Duration={step.duration.asXml}
        PowerLow={(step.ftpPercentStart / 100f).toString}
        PowerHigh={(step.ftpPercentEnd / 100f).toString} />
  }

  implicit val xmlEncoder: XmlEncoder[WorkoutStep] = {
    case step: Warmup => step.asXml
    case step: SteadyState => step.asXml
    case step: Ramp => step.asXml
    case step: Cooldown => step.asXml
  }

}
