package ahlers.training.tools.conversion

import ahlers.training.tools.ToolsApp
import java.net.URI
import scala.util.Try
import zio._
import zio.cli.Args
import zio.cli.Command
import zio.cli.HelpDoc
import zio.cli.Options

case class ConversionApp(
  dryRun: ToolsApp.DryRun,
  input: ConversionApp.Input,
  output: ConversionApp.Output,
) extends ZIOAppDefault { self =>

  val run = for {
    _ <- ZIO.logInfo(s"Performing $dryRun conversion of $input to $output.")
  } yield ()

}

object ConversionApp {

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

  val options: Options[(ToolsApp.DryRun, InputFormat, OutputFormat)] =
    ToolsApp.DryRun.options ++
      InputFormat.options ++
      OutputFormat.options

  val args: Args[(InputLocation, OutputLocation)] =
    InputLocation.args ++
      OutputLocation.args

  val helpDoc: HelpDoc = HelpDoc.p("Converts given workout or activity into a different format.")

  val command: Command[ConversionApp] = Command("convert", options, args).withHelp(helpDoc).map {
    case ((dryRun, inputFormat, outputFormat), (inputLocation, outputLocation)) =>
      ConversionApp(
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
