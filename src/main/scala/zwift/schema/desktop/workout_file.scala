package zwift.schema.desktop

import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._
import zwift.schema.desktop.workout_file.Step
import zwift.schema.desktop.workout_file.Tag

case class workout_file(
  author: String,
  name: String,
  description: String,
  sportType: String,
  tags: Seq[Tag],
  workout: Seq[Step],
)

object workout_file {

  case class Tag(name: String)
  object Tag {
    implicit val xmlEncoder: XmlEncoder[Tag] = tag =>
      <tag name ={tag.name} />
  }

  case class workout(
    steps: Step,
  )

  sealed trait Step
  object Step {

    case class SteadyState(
      Duration: Int,
      Power: Float,
    ) extends Step

    object SteadyState {
      implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
        <SteadyState
          Duration={step.Duration.toString}
          Power={step.Power.toString} />
    }

    implicit val xmlEncoder: XmlEncoder[Step] = {
      case step: SteadyState => step.asXml
    }

  }

  implicit val xmlEncoder: XmlEncoder[workout_file] = file =>
    <workout_file>
      <author>{file.author}</author>
      <name>{file.name}</name>
      <description>{file.description}</description>
      <sportType>{file.sportType}</sportType>
      <tags>{file.tags.map(_.asXml)}</tags>
      <workout>{file.workout.map(_.asXml)}</workout>
    </workout_file>

}
