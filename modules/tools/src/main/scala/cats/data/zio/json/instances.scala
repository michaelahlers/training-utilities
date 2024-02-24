package cats.data.zio.json

import cats.data.NonEmptyList
import zio.json.JsonDecoder

object instances {

  implicit def zioDecoderNonEmptyList[A: JsonDecoder]: JsonDecoder[NonEmptyList[A]] =
    JsonDecoder[List[A]].mapOrFail(NonEmptyList.fromList(_)
      .map(Right(_))
      .getOrElse(Left("Expected non-empty list.")))

}
