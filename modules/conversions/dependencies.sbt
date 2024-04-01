/**
 * Simplifies working with real-world HTML and XML.
 * @see [[https://jsoup.org]]
 */
libraryDependencies +=
  "org.jsoup" % "jsoup" % "1.17.2"

/**
 * Patch and modify deeply nested case classes.
 * @see [[https://github.com/softwaremill/quicklens]]
 */
libraryDependencies +=
  "com.softwaremill.quicklens" %% "quicklens" % "1.9.7"

/**
 * ScalaTest bindings for Cats.
 * @see [[https://github.com/IronCoreLabs/cats-scalatest]]
 */
libraryDependencies +=
  "com.ironcorelabs" %% "cats-scalatest" % "3.1.1" % Test

/**
 * Readable deltas for Scala case classes.
 * @see [[https://github.com/softwaremill/diffx]]
 */
libraryDependencies ++=
  "com.softwaremill.diffx"   %% "diffx-cats"             % "0.9.0" % Test ::
    "com.softwaremill.diffx" %% "diffx-scalatest-must"   % "0.9.0" % Test ::
    "com.softwaremill.diffx" %% "diffx-scalatest-should" % "0.9.0" % Test ::
    Nil

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
    "org.scalatestplus" %% "scalacheck-1-17"      % "3.2.18.0" % Test ::
    "com.spotify"       %% "magnolify-scalacheck" % "0.7.0"    % Test ::
    Nil

/**
 * ScalaTest is the most flexible and most popular testing tool in the Scala ecosystem.
 * @see [[https://www.scalatest.org/]]
 */
libraryDependencies +=
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
