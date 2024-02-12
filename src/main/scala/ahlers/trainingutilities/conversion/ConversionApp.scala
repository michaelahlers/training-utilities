package ahlers.trainingutilities.conversion

import zio._

object ConversionApp extends ZIOAppDefault {

  val run =
    for {
      settings <- ConversionSettings.load
    } yield pprint.log(settings)

}
