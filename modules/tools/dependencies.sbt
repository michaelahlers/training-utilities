/**
 * The standard Scala XML library.
 * @see [[https://github.com/scala/scala-xml]]
 */
libraryDependencies +=
  "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

/**
 * Cats is a library which provides abstractions for functional programming in the Scala programming language.
 * @see [[https://typelevel.org/cats/]]
 */
libraryDependencies +=
  "org.typelevel" %% "cats-core" % "2.10.0"

//libraryDependencies +=
//  "org.xerial" % "sqlite-jdbc" % "3.44.1.0"

/**
 * Simple, safe and intuitive Scala I/O.
 * @see [[https://github.com/pathikrit/better-files]]
 */
libraryDependencies +=
  "com.github.pathikrit" %% "better-files" % "3.9.2"

///**
// * @todo Might not be needed; consider removing.
// */
//libraryDependencies +=
//  "com.lihaoyi" %% "requests" % "0.8.0"

///**
// * @todo Might not be needed; consider removing.
// */
//libraryDependencies +=
//  "com.lihaoyi" %% "scalatags" % "0.12.0"

///**
// * Garmin's SDK for the Flexible and Interoperable Data Transfer (FIT) protocol, designed specifically for the storing and sharing of data that originates from sport, fitness and health devices.
// *
// * @see [[https://developer.garmin.com/fit/]]
// * @see [[https://mvnrepository.com/artifact/com.garmin/fit]]
// */
//libraryDependencies +=
//  "com.garmin" % "fit" % "21.126.0"

/**
 * Patch and modify deeply nested case classes.
 * @see [[https://github.com/softwaremill/quicklens]]
 */
libraryDependencies +=
  "com.softwaremill.quicklens" %% "quicklens" % "1.9.6" % Test

///**
// * A Scala library to pretty-print values and types.
// * @see [[https://github.com/com-lihaoyi/PPrint]]
// */
//libraryDependencies +=
//  "com.lihaoyi" %% "pprint" % "0.8.1"

/**
 * Simple logging for ZIO.
 * @see [[https://github.com/zio/zio-logging]]
 */
libraryDependencies +=
  "dev.zio" %% "zio-streams" % "2.0.21"

/**
 * Simple logging for ZIO.
 * @see [[https://github.com/zio/zio-logging]]
 */
libraryDependencies +=
  "dev.zio" %% "zio-logging" % "2.2.2"

libraryDependencies ++=
  // "dev.zio"   %% "zio-config"          % "4.0.1" ::
  "dev.zio"   %% "zio-config-magnolia" % "4.0.1" ::
    "dev.zio" %% "zio-config-typesafe" % "4.0.1" ::
    // "dev.zio" %% "zio-config-yaml"     % "4.0.1" ::
    Nil

/**
 * Simplifies working with real-world HTML and XML.
 * @see [[https://jsoup.org]]
 */
libraryDependencies +=
  "org.jsoup" % "jsoup" % "1.17.2"

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
  "org.scalacheck"      %% "scalacheck"           % "1.17.0"   % Test ::
    "org.scalatestplus" %% "scalacheck-1-17"      % "3.2.17.0" % Test ::
    "com.spotify"       %% "magnolify-scalacheck" % "0.7.0"    % Test ::
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
  "com.softwaremill.diffx"   %% "diffx-cats"             % "0.9.0" % Test ::
    "com.softwaremill.diffx" %% "diffx-scalatest-must"   % "0.9.0" % Test ::
    "com.softwaremill.diffx" %% "diffx-scalatest-should" % "0.9.0" % Test ::
    Nil

libraryDependencies ++=
  "dev.zio"   %% "zio-test"          % "2.1-RC1" % Test ::
    "dev.zio" %% "zio-test-sbt"      % "2.1-RC1" % Test ::
    "dev.zio" %% "zio-test-magnolia" % "2.1-RC1" % Test ::
    Nil

/**
 * ScalaTest bindings for Cats.
 * @see [[https://github.com/IronCoreLabs/cats-scalatest]]
 */
libraryDependencies +=
  "com.ironcorelabs" %% "cats-scalatest" % "3.0.5" % Test
