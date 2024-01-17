package scala.xml.encoding

import scala.xml.NodeSeq

object syntax {

  implicit class XmlEncoderOps[A](private val self: A) extends AnyVal {
    def asXml(implicit encoder: XmlEncoder[A]): NodeSeq = encoder(self)
  }

}
