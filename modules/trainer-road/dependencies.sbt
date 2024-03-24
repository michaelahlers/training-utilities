/**
 * Cats is a library which provides abstractions for functional programming in the Scala programming language.
 * @see [[https://typelevel.org/cats/]]
 */
libraryDependencies +=
  "org.typelevel" %% "cats-core" % "2.10.0"

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
 * A fast and secure JSON library with tight ZIO integration.
 * @see [[https://zio.dev/zio-json]]
 */
libraryDependencies +=
  "dev.zio" %% "zio-json" % "0.6.2"

/**
 * Readable deltas for Scala case classes.
 * @see [[https://github.com/softwaremill/diffx]]
 */
libraryDependencies +=
  "com.softwaremill.diffx" %% "diffx-cats" % "0.9.0" % Test

/**
 * Property-based testing for Scala, with support for ScalaTest.
 * Also includes support for automatic derivation of ScalaCheck instances.
 *
 * @see [[https://scalacheck.org]]
 * @see [[https://scalatest.org/plus/scalacheck]]
 * @see [[https://github.com/spotify/magnolify]]
 */
libraryDependencies ++=
  "org.scalacheck" %% "scalacheck"           % "1.17.0" % Test ::
    "com.spotify"  %% "magnolify-scalacheck" % "0.7.0"  % Test ::
    Nil
