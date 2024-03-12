package zwift.desktop

import better.files.File
import zio.ZIO
import zio.ZLayer
import zio.prelude.NonEmptyList

case class WithZwiftWorkoutsFolders(
  zwiftWorkoutsFolders: NonEmptyList[File],
)

object WithZwiftWorkoutsFolders {

  val zwiftWorkoutsFolders: ZIO[Any, Throwable, NonEmptyList[File]] = WithZwiftDocumentsFolder
    .zwiftDocumentsFolder
    .map(_ / "Workouts")
    .tap { folder =>
      if (folder.exists && folder.isDirectory) ZIO.unit
      else ZIO.fail(new IllegalStateException(s"""Couldn't find Zwift documents folder "$folder"."""))
    }
    .flatMap { workoutsFolder =>
      ZIO.attempt(workoutsFolder.children.filter(_.isDirectory).toSeq)
        .flatMap(NonEmptyList.fromIterableOption(_) match {
          case Some(userFolders) => ZIO.succeed(userFolders)
          case None              => ZIO.fail(new IllegalStateException(s"""Couldn't find any Zwift workouts folders in "$workoutsFolder"."""))
        })
    }

  val live: ZLayer[Any, Throwable, WithZwiftWorkoutsFolders] = ZLayer
    .fromZIO(zwiftWorkoutsFolders
      .map(WithZwiftWorkoutsFolders(_)))

}
