package ahlers.training.tools

import zio.cli._

sealed trait DryRunFlag
object DryRunFlag {
  case object IsDry extends DryRunFlag
    case object IsWet extends DryRunFlag

  val options:Options[DryRunFlag] = Options
    .boolean("dry-run").alias("n")
    .map {
      case true => IsDry
      case false => IsWet
    }

}
