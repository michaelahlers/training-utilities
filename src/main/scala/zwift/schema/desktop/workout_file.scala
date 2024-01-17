package zwift.schema.desktop

import scala.xml.encoding.XmlEncoder
import scala.xml.encoding.syntax._
import zwift.schema.desktop.workout_file.Tag

case class workout_file(
  author: String,
  name: String,
  description: String,
  sportType: String,
  tags: Seq[Tag],
)

object workout_file {

  case class Tag(name: String)
  object Tag {
    implicit val xmlEncoder: XmlEncoder[Tag] = tag =>
      <tag name ={tag.name} />
  }

  implicit val xmlEncoder: XmlEncoder[workout_file] = file =>
    <workout_file>
      <author>{file.author}</author>
      <name>{file.name}</name>
      <description>{file.description}</description>
      <sportType>{file.sportType}</sportType>
      <tags>{file.tags.map(_.asXml)}</tags>
    </workout_file>

}
