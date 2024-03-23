ThisBuild / githubWorkflowJavaVersions :=
  JavaSpec.temurin("21") ::
    Nil

ThisBuild / githubWorkflowPublishTargetBranches := Nil

ThisBuild / githubWorkflowBuildRunsOnExtraLabels :=
  "macos-latest" ::
    "windows-latest" ::
    Nil
