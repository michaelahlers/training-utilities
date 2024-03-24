import sbtghactions.WorkflowStep.Sbt

ThisBuild / githubWorkflowJavaVersions :=
  JavaSpec.temurin("21") ::
    Nil

ThisBuild / githubWorkflowPublishTargetBranches := Nil

ThisBuild / githubWorkflowOSes :=
  "ubuntu-latest" ::
    "macos-latest" ::
    "windows-latest" ::
    Nil

ThisBuild / githubWorkflowBuildPreamble +=
  Sbt(
    commands = List("unusedCompileDependenciesTest"),
    name = Some("Check for unused compile-time dependencies."),
  )
