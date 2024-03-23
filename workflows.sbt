import sbtghactions.WorkflowStep.Sbt

ThisBuild / githubWorkflowJavaVersions :=
  JavaSpec.temurin("21") ::
    Nil

ThisBuild / githubWorkflowPublishTargetBranches := Nil

ThisBuild / githubWorkflowBuildRunsOnExtraLabels :=
  "macos-latest" ::
    "windows-latest" ::
    Nil

ThisBuild / githubWorkflowBuildPreamble +=
  Sbt(
    commands =
      "unusedCompileDependenciesTest" ::
        Nil,
    name = Some("Check for unused compile-time dependencies."),
  )
