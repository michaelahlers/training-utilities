package zwift.schema.desktop

import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._

sealed trait WorkoutStep
object WorkoutStep {

  case class SteadyState(
    duration: Int,
    power: Float,
  ) extends WorkoutStep

  object SteadyState {
    implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
      <SteadyState
        Duration={step.duration.toString}
        Power={step.power.toString} />
  }

  implicit val xmlEncoder: XmlEncoder[WorkoutStep] = {
    case step: SteadyState => step.asXml
  }

}
