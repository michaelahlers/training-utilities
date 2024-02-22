package ahlers.training.tools.conversion

import ahlers.training.tools.DryRunFlag
import ahlers.training.tools.ToolsTask
import java.net.URI
import scala.util.control.NonFatal
import zio.cli.Args
import zio.cli.Command
import zio.cli.HelpDoc
import zio.cli.Options

case class ConversionToolsTask(
  dryRunFlag: DryRunFlag,
  input: ConversionToolsTask.Input,
  output: ConversionToolsTask.Output,
) extends ToolsTask

object ConversionToolsTask extends ToolsTask {

  case class Input(toLocation: URI)
  object Input {
    val args: Args[Input] = Args
      .text("input")
      .mapOrFail { location =>
        try Right(new URI(location))
        catch {
          case NonFatal(_) =>
            Left(HelpDoc.p(s"""Can't parse input location, "$location", as a URI."""))
        }
      }
      .map(Input(_))
  }

  case class Output(toLocation: URI)
  object Output {
    val args: Args[Output] = Args
      .text("output")
      .mapOrFail { location =>
        try Right(new URI(location))
        catch {
          case NonFatal(_) =>
            Left(HelpDoc.p(s"""Can't parse output location, "$location", as a URI."""))
        }
      }
      .map(Output(_))
  }

  val options: Options[DryRunFlag] = DryRunFlag.options
  val args: Args[(Input, Output)] = Input.args ++ Output.args
  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout or activity into a different format.")

  val command: Command[ConversionToolsTask] = Command("convert", options, args).withHelp(helpDoc).map {
    case (dryRunFlag, (input, output)) => ConversionToolsTask(dryRunFlag, input, output)
  }

}
