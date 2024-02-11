package scala.xml.encoding

import cats.data.NonEmptyList
import scala.xml.NodeSeq
import scala.xml.encoding.syntax._

object instances {

  implicit def xmlEncoderSeq[C[X] <: Iterable[X], A: XmlEncoder]: XmlEncoder[C[A]] = as =>
    NodeSeq.fromSeq(as.flatMap(_.asXml).toSeq)

  implicit def xmlEncoderNonEmptyList[A: XmlEncoder]: XmlEncoder[NonEmptyList[A]] = as =>
    as.toList.asXml

}
