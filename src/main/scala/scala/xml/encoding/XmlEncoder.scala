package scala.xml.encoding

import scala.xml.NodeSeq

trait XmlEncoder[A] extends (A => NodeSeq)
