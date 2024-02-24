package ahlers.training.tools.conversion

import ahlers.training.tools.ToolsApp
import java.net.URI
import java.net.URL
import scala.util.Try
import zio._
import zio.cli.Args
import zio.cli.Command
import zio.cli.HelpDoc
import zio.cli.Options
import zio.stream.ZSink
import zio.stream.ZStream

case class ConvertApp(
  dryRun: ToolsApp.DryRun,
  input: ConvertApp.Input,
  output: ConvertApp.Output,
  conversion: Conversion,
) extends ZIOAppDefault { self =>

  private val inputF: ZStream[Any, Throwable, Byte]            = ZStream.fromFileURI(input.location.toUri)
  private val outputF: ZSink[Any, Throwable, Byte, Byte, Long] = ZSink.fromFileURI(output.location.toUri)

  val run = for {
    _ <- ZIO.logInfo(s"Performing $dryRun conversion of $input to $output using $conversion.")
    _ <- inputF.via(conversion.total).run(outputF)
  } yield ()

}

object ConvertApp {

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

  case class InputLocation(toUri: URI) {
    def toUrl: URL = toUri.toURL
  }

  object InputLocation {
    val args: Args[InputLocation] = Args
      .text("input-location")
      .mapOrFail { location =>
        Try(Right(new URI(location)))
          .getOrElse(Left(HelpDoc.p(s"""Can't parse input location, "$location", as a URI.""")))
      }
      .map(InputLocation(_))
  }

  case class OutputLocation(toUri: URI) {
    def toUrl: URL = toUri.toURL
  }

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
    location: InputLocation,
  )

  case class Output(
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

  val command: Command[ConvertApp] = Command("convert", options, args).withHelp(helpDoc).map {
    case ((dryRun, InputFormat.TrainerRoadWorkout, OutputFormat.ZwiftWorkout), (inputLocation, outputLocation)) =>
      ConvertApp(
        dryRun = dryRun,
        input = Input(
          location = inputLocation,
        ),
        output = Output(
          location = outputLocation,
        ),
        conversion = Conversion.FromTrainerRoadWorkoutToZwiftWorkout,
      )
  }

}
