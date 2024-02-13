val `tools` =
  project in file("tools")

val `trainerroad-utilities` =
  (project in file("."))
    .aggregate(`tools`)
