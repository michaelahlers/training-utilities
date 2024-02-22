package ahlers.training.tools

import zio.cli._

trait ToolsTask
object ToolsTask {

  sealed trait DryRun
  object DryRun {
    case object IsDry extends DryRun
    case object IsWet extends DryRun

    val options: Options[DryRun] = Options
      .boolean("dry-run").alias("n")
      .map {
        case true => IsDry
        case false => IsWet
      }

  }

}
