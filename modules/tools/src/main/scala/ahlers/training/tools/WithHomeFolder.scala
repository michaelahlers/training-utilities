package ahlers.training.tools

import better.files.File
import zio.ZIO
import zio.ZLayer

case class WithHomeFolder(
  homeFolder: File,
)

object WithHomeFolder {

  val homeFolder: ZIO[Any, Throwable, File] = ZIO
    .attempt {

      /**
       * If the `OneDrive` environment variable is set, then running on Windows and the logged in user (and, by extension, Zwift) stores documents there.
       */
      sys.props.get("OneDrive") match {
        case None           => File.home
        case Some(oneDrive) => File(oneDrive)
      }
    }
    .tap { folder =>
      if (folder.exists && folder.isDirectory) ZIO.unit
      else ZIO.fail(new IllegalStateException(s"""Couldn't find home folder "$folder"."""))
    }

  val live: ZLayer[Any, Throwable, WithHomeFolder] = ZLayer
    .fromZIO(homeFolder
      .map(WithHomeFolder(_)))

}
