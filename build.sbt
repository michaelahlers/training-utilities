val `tools` =
  project in file("modules") / "tools"

val `trainerroad-utilities` =
  (project in file("."))
    .aggregate(`tools`)
