package zwift.schema.desktop

import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._
import zwift.schema.desktop.WorkoutFile.Step
import zwift.schema.desktop.WorkoutFile.Tag

case class WorkoutFile(
  author: String,
  name: String,
  description: String,
  sportType: String,
  tags: Seq[Tag],
  workout: Seq[Step],
)

object WorkoutFile {

  case class Tag(name: String)
  object Tag {
    implicit val xmlEncoder: XmlEncoder[Tag] = tag =>
      <tag name={tag.name} />
  }

  case class workout(
    steps: Step,
  )

  sealed trait Step
  object Step {

    case class SteadyState(
      duration: Int,
      power: Float,
    ) extends Step

    object SteadyState {
      implicit val xmlEncoder: XmlEncoder[SteadyState] = step =>
        <SteadyState
          Duration={step.duration.toString}
          Power={step.power.toString} />
    }

    implicit val xmlEncoder: XmlEncoder[Step] = {
      case step: SteadyState => step.asXml
    }

  }

  implicit val xmlEncoder: XmlEncoder[WorkoutFile] = file =>
    <workout_file>
      <author>{file.author}</author>
      <name>{file.name}</name>
      <description>{file.description}</description>
      <sportType>{file.sportType}</sportType>
      <tags>{file.tags.map(_.asXml)}</tags>
      <workout>{file.workout.map(_.asXml)}</workout>
    </workout_file>

}
