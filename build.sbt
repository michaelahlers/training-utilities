lazy val `tools` =
  (project in file("modules") / "tools")
    .dependsOn(
      `trainer-road`,
      `zio-diffx` % Test,
    )

lazy val `trainer-road` =
  (project in file("modules") / "trainer-road")
    .dependsOn(
      `zio-diffx` % Test,
    )

lazy val `zio-diffx` =
  project in file("modules") / "zio-diffx"

lazy val `zio-json` =
  project in file("modules") / "zio-json"

val `training-utilities` =
  (project in file("."))
    .aggregate(
      `tools`,
      `trainer-road`,
      `zio-diffx`,
      `zio-json`,
    )
