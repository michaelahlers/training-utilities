lazy val `tools` =
  (project in file("modules") / "tools")
    .dependsOn(
      `zio-diffx` % Test,
    )

lazy val `zio-diffx` =
  project in file("modules") / "zio-diffx"

val `training-utilities` =
  (project in file("."))
    .aggregate(
      `tools`,
      `zio-diffx`,
    )
