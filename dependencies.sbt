ThisBuild / scalaVersion := "2.13.12"

/**
 * Cats is a library which provides abstractions for functional programming in the Scala programming language.
 * @see [[https://typelevel.org/cats/]]
 */
libraryDependencies +=
  "org.typelevel" %% "cats-core" % "2.10.0"

/**
 * A JSON library for Scala powered by Cats.
 * @see [[https://circe.github.io/circe/]]
 */
ThisBuild / libraryDependencies ++=
  "io.circe" %% "circe-core" % "0.13.0" ::
    "io.circe" %% "circe-generic" % "0.13.0" ::
    "io.circe" %% "circe-generic-extras" % "0.13.0" ::
    "io.circe" %% "circe-parser" % "0.13.0" ::
    Nil

libraryDependencies +=
  "org.xerial" % "sqlite-jdbc" % "3.44.1.0"

/**
 * Simple, safe and intuitive Scala I/O.
 * @see [[https://github.com/pathikrit/better-files]]
 */
libraryDependencies +=
  "com.github.pathikrit" %% "better-files" % "3.9.2"

/**
 * The standard Scala XML library.
 * @see [[https://github.com/scala/scala-xml]]
 */
libraryDependencies +=
  "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

/**
 * @todo Might not be needed; consider removing.
 */
libraryDependencies +=
  "com.lihaoyi" %% "requests" % "0.8.0"

/**
 * @todo Might not be needed; consider removing.
 */
libraryDependencies +=
  "com.lihaoyi" %% "scalatags" % "0.12.0"

/**
 * Garmin's SDK for the Flexible and Interoperable Data Transfer (FIT) protocol, designed specifically for the storing and sharing of data that originates from sport, fitness and health devices.
 *
 * @see [[https://developer.garmin.com/fit/]]
 * @see [[https://mvnrepository.com/artifact/com.garmin/fit]]
 */
libraryDependencies +=
  "com.garmin" % "fit" % "21.126.0"

/**
 * Patch and modify deeply nested case classes.
 * @see [[https://github.com/softwaremill/quicklens]]
 */
libraryDependencies +=
  "com.softwaremill.quicklens" %% "quicklens" % "1.9.6"

/**
 * A Scala library to pretty-print values and types.
 * @see [[https://github.com/com-lihaoyi/PPrint]]
 */
libraryDependencies +=
  "com.lihaoyi" %% "pprint" % "0.8.1"

/**
 * Squants is a framework of data types and a domain specific language (DSL) for representing Quantities, their Units of Measure, and their Dimensional relationships.
 * @see [[https://squants.com]]
 */
libraryDependencies +=
  "org.typelevel" %% "squants" % "1.8.3"

/**
 * Scalactic provides constructs related to quality that are useful in both production code and tests.
 * @see [[https://www.scalactic.org/]]
 */
libraryDependencies +=
  "org.scalactic" %% "scalactic" % "3.2.17"

/**
 * ScalaTest is the most flexible and most popular testing tool in the Scala ecosystem.
 * @see [[https://www.scalatest.org/]]
 */
libraryDependencies +=
  "org.scalatest" %% "scalatest" % "3.2.17" % Test

/**
 * Property-based testing for Scala, with support for ScalaTest.
 * Also includes support for automatic derivation of ScalaCheck instances.
 *
 * @see [[https://scalacheck.org]]
 * @see [[https://scalatest.org/plus/scalacheck]]
 * @see [[https://github.com/spotify/magnolify]]
 */
libraryDependencies ++=
  "org.scalacheck" %% "scalacheck" % "1.14.1" % Test ::
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.17.0" % Test ::
    "com.spotify" %% "magnolify-scalacheck" % "0.7.0" % Test ::
    Nil

/**
 * Native Scala mocking framework.
 * @see [[https://www.scalatest.org/]]
 */
libraryDependencies ++=
  "org.scalamock" %% "scalamock" % "5.2.0" % Test ::
    Nil

/**
 * Readable deltas for Scala case classes.
 * @see [[https://github.com/softwaremill/diffx]]
 */
libraryDependencies ++=
  "com.softwaremill.diffx" %% "diffx-cats" % "0.9.0" % Test ::
    "com.softwaremill.diffx" %% "diffx-scalatest-must" % "0.9.0" % Test ::
    "com.softwaremill.diffx" %% "diffx-scalatest-should" % "0.9.0" % Test ::
    Nil
