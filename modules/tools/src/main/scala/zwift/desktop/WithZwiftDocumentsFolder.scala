package zwift.desktop

import ahlers.training.tools.WithHomeFolder
import better.files.File
import zio.ZIO
import zio.ZLayer

case class WithZwiftDocumentsFolder(
  zwiftDocumentsFolder: File,
)

object WithZwiftDocumentsFolder {

  val zwiftDocumentsFolder: ZIO[Any, Throwable, File] = WithHomeFolder
    .homeFolder
    .map(_ / "Documents" / "Zwift")
    .tap { folder =>
      if (folder.exists && folder.isDirectory) ZIO.unit
      else ZIO.fail(new IllegalStateException(s"""Couldn't find Zwift documents folder "$folder"."""))
    }

  val live: ZLayer[Any, Throwable, WithZwiftDocumentsFolder] = ZLayer
    .fromZIO(zwiftDocumentsFolder
      .map(WithZwiftDocumentsFolder(_)))

}
