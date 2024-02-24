package zwift.schema.desktop

import cats.data.NonEmptyList
import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.instances._
import scala.xml.encoding.syntax._
import zwift.schema.desktop.WorkoutFile.Tag

case class WorkoutFile(
  author: String,
  name: String,
  description: String,
  sportType: String,
  tags: Seq[Tag],
  workout: NonEmptyList[WorkoutStep],
)

object WorkoutFile {

  case class Tag(name: String)
  object Tag {

    /**
     * @see [[https://github.com/h4l/zwift-workout-file-reference/blob/master/zwift_workout_file_tag_reference.md#element-tag]]
     */
    implicit val xmlEncoder: XmlEncoder[Tag] = tag =>
      <tag name={tag.name} />
  }

  /**
   * @see [[https://github.com/h4l/zwift-workout-file-reference/blob/master/zwift_workout_file_tag_reference.md#element-workout_file]]
   */
  implicit val xmlEncoder: XmlEncoder[WorkoutFile] = file =>
    <workout_file>
      <author>{file.author}</author>
      <name>{file.name}</name>
      <description>{file.description}</description>
      <sportType>{file.sportType}</sportType>
      <tags>{file.tags.asXml}</tags>
      <workout>{file.workout.asXml}</workout>
    </workout_file>

}
