ThisBuild / githubWorkflowJavaVersions :=
  JavaSpec.temurin("21") ::
    Nil

ThisBuild / githubWorkflowPublishTargetBranches := Nil

ThisBuild / githubWorkflowOSes :=
  "macos-latest" ::
    "ubuntu-latest" ::
    "windows-latest" ::
    Nil
