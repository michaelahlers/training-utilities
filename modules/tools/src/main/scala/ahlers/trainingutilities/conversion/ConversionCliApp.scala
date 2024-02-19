package ahlers.trainingutilities.conversion

import zio._

object ConversionCliApp extends ZIOAppDefault {

  val run =
    for {
      settings <- ConversionSettings.load
    } yield pprint.log(settings)

}
