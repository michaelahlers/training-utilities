/**
 * Readable deltas for Scala case classes.
 * @see [[https://github.com/softwaremill/diffx]]
 */
libraryDependencies +=
  "com.softwaremill.diffx" %% "diffx-core" % "0.9.0"

libraryDependencies +=
  "dev.zio" %% "zio-test" % "2.1.11"

libraryDependencies +=
  "dev.zio" %% "zio-test-sbt" % "2.1-RC1" % Test
