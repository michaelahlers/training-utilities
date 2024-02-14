val `tools` =
  project in file("modules") / "tools"

val `zio-diffx` =
  project in file("modules") / "zio-diffx"

val `trainerroad-utilities` =
  (project in file("."))
    .aggregate(
      `tools`,
      `zio-diffx`,
    )
