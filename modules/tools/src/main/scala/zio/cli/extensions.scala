package zio.cli

import java.net.URI
import java.nio.file.Paths
import scala.util.control.NonFatal
import zio.cli.HelpDoc.p

object extensions {

  implicit class OptionsTypeExtensions(private val self: Options.type) extends AnyVal {
    def uri(name: String): Options[URI] = Options
      .text(name)
      .mapOrFail { location =>
        /** Parse valid [[URI]]. */
        try Right(new URI(location))
        catch {
          case NonFatal(cause) =>
            Left(ValidationError(
              validationErrorType = ValidationErrorType.InvalidValue,
              error = p(s"""Location "$location" isn't a valid path or URI. (${cause.getMessage})"""),
            ))
        }
      }
      .mapOrFail {
        case location if location.isAbsolute => Right(location)
        case location                        =>
          /** Attempt resolve to absolute [[URI]] given special case for file [[Paths]]. */
          try Right(Paths.get(location.getPath).toUri)
          catch {
            case NonFatal(cause) =>
              Left(ValidationError(
                validationErrorType = ValidationErrorType.InvalidValue,
                error = p(s"""Couldn't find file at "$location". (${cause.getMessage})"""),
              ))
          }
      }
  }

}
