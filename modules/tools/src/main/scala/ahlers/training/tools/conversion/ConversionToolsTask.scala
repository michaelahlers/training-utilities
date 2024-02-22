package ahlers.training.tools.conversion

import ahlers.training.tools.ToolsTask
import java.net.URI
import scala.util.Try
import zio.cli.Args
import zio.cli.Command
import zio.cli.HelpDoc
import zio.cli.Options

case class ConversionToolsTask(
  dryRun: ToolsTask.DryRun,
  input: ConversionToolsTask.Input,
  output: ConversionToolsTask.Output,
) extends ToolsTask

object ConversionToolsTask extends ToolsTask {

  sealed trait InputFormat
  object InputFormat {

    case object TrainerRoadWorkout extends InputFormat

    val options: Options[InputFormat] = Options
      .text("input-format")
      .map {
        case "trainer-road-workout" => TrainerRoadWorkout
      }

  }

  sealed trait OutputFormat
  object OutputFormat {

    case object ZwiftWorkout extends OutputFormat

    val options: Options[OutputFormat] = Options
      .text("output-format")
      .map {
        case "zwift-workout" => ZwiftWorkout
      }

  }

  case class InputLocation(toUri: URI)
  object InputLocation {
    val args: Args[InputLocation] = Args
      .text("input-location")
      .mapOrFail { location =>
        Try(Right(new URI(location)))
          .getOrElse(Left(HelpDoc.p(s"""Can't parse input location, "$location", as a URI.""")))
      }
      .map(InputLocation(_))
  }

  case class OutputLocation(toUri: URI)
  object OutputLocation {
    val args: Args[OutputLocation] = Args
      .text("output-location")
      .mapOrFail { location =>
        Try(Right(new URI(location)))
          .getOrElse(Left(HelpDoc.p(s"""Can't parse output location, "$location", as a URI.""")))
      }
      .map(OutputLocation(_))
  }

  case class Input(
    format: InputFormat,
    location: InputLocation,
  )

  case class Output(
    format: OutputFormat,
    location: OutputLocation,
  )

  val options: Options[(ToolsTask.DryRun, InputFormat, OutputFormat)] =
    ToolsTask.DryRun.options ++
      InputFormat.options ++
      OutputFormat.options

  val args: Args[(InputLocation, OutputLocation)] =
    InputLocation.args ++
      OutputLocation.args

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout or activity into a different format.")

  val command: Command[ConversionToolsTask] = Command("convert", options, args).withHelp(helpDoc).map {
    case ((dryRun, inputFormat, outputFormat), (inputLocation, outputLocation)) =>
      ConversionToolsTask(
        dryRun = dryRun,
        input = Input(
          format = inputFormat,
          location = inputLocation,
        ),
        output = Output(
          format = outputFormat,
          location = outputLocation,
        ),
      )
  }

}
