val `tools` =
  project in file("modules") / "tools"

val `zio-diffx` =
  RootProject(uri("https://github.com/bbarker/zio-diffx.git"))

val `trainerroad-utilities` =
  (project in file("."))
    .aggregate(
      `tools`,
      `zio-diffx`,
    )
