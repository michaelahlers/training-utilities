package ahlers.trainingutilities.conversion.diffx

import ahlers.trainingutilities.conversion.ConversionSettings
import com.softwaremill.diffx._
import com.softwaremill.diffx.generic.auto._

object instances {

  implicit val diffConversionSettings:Diff[ConversionSettings] = Diff.summon

}
