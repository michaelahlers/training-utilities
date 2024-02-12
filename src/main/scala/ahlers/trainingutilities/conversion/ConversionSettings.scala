package ahlers.trainingutilities.conversion

import better.files.Resource
import com.typesafe.config.ConfigFactory
import zio._
import zio.config.magnolia._
import zio.config.typesafe._

case class ConversionSettings(
  home: Option[String],
  oneDrive: Option[String],
)

object ConversionSettings {

  val load: ZIO[Any, Throwable, ConversionSettings] =
    ZIO.attempt(ConfigFactory
      .parseURL(Resource.my.getUrl("application.conf"))
      .resolve())
      .flatMap(ConfigProvider
        .fromTypesafeConfig(_)
        .load(deriveConfig[ConversionSettings]))

}
