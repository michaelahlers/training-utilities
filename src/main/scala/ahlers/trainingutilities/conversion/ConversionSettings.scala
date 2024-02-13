package ahlers.trainingutilities.conversion

import better.files.Resource
import com.typesafe.config.ConfigFactory
import zio._
import zio.config.magnolia._
import zio.config.typesafe._

case class ConversionSettings(
  environment: ConversionSettings.Environment,
)

object ConversionSettings {

  case class Environment(
    macOS: Environment.MacOS,
    windows: Environment.Windows,
  )

  object Environment {

    case class MacOS(
      home: Option[String],
    )

    case class Windows(
      home: Option[String],
      oneDrive: Option[String],
    )

  }

  val load: ZIO[Any, Throwable, ConversionSettings] =
    ZIO.attempt(ConfigFactory
      .parseURL(Resource.my.getUrl("application.conf"))
      .resolve())
      .flatMap(ConfigProvider
        .fromTypesafeConfig(_)
        .load(deriveConfig[ConversionSettings]))

}
