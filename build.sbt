lazy val `conversions` =
  (project in file("modules") / "conversions")
    .dependsOn(
      `trainer-road`,
      `trainer-road` % "test->test",
      `zio-cli`,
      `zwift`,
      `zwift`     % "test->test",
      `zio-diffx` % Test,
    )

lazy val `tools` =
  (project in file("modules") / "tools")
    .dependsOn(
      `conversions`,
      `trainer-road`,
      `trainer-road` % "test->test",
      `zio-cli`,
      `zwift`,
      `zwift`     % "test->test",
      `zio-diffx` % Test,
    )

lazy val `scala-xml` =
  project in file("modules") / "scala-xml"

lazy val `trainer-road` =
  (project in file("modules") / "trainer-road")
    .dependsOn(
      `zio-json`,
    )

lazy val `zio-cli` =
  project in file("modules") / "zio-cli"

lazy val `zio-diffx` =
  project in file("modules") / "zio-diffx"

lazy val `zio-json` =
  project in file("modules") / "zio-json"

lazy val `zwift` =
  (project in file("modules") / "zwift")
    .dependsOn(
      `scala-xml`,
    )

val `training-utilities` =
  (project in file("."))
    .aggregate(
      `tools`,
      `scala-xml`,
      `trainer-road`,
      `zio-cli`,
      `zio-diffx`,
      `zio-json`,
      `zwift`,
    )
