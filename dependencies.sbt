ThisBuild / scalaVersion := "2.13.12"

ThisBuild / libraryDependencies ++=
  "io.circe" %% "circe-core" % "0.13.0" ::
    "io.circe" %% "circe-generic" % "0.13.0" ::
    "io.circe" %% "circe-generic-extras" % "0.13.0" ::
    "io.circe" %% "circe-parser" % "0.13.0" ::
    Nil

//libraryDependencies +=
//  "org.xerial" %% "sqlite-jdbc" % "3.39.2.1"
