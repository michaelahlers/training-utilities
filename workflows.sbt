import sbtghactions.Ref.Tag
import sbtghactions.RefPredicate.StartsWith
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

ThisBuild / githubWorkflowBuildPreamble ++= {
  val unusedCompileDependencies = Sbt(
    commands = List("unusedCompileDependenciesTest"),
    name = Some("Check for unused compile-time dependencies."),
  )

  val scalaFmtCheck = Sbt(
    commands = List("scalafmtCheck"),
    name = Some("Check Scala code formatting."),
  )

  val scalaFmtSbtCheck = Sbt(
    commands = List("scalafmtSbtCheck"),
    name = Some("Check sbt project formatting."),
  )

  unusedCompileDependencies ::
    scalaFmtCheck ::
    scalaFmtSbtCheck ::
    Nil
}

ThisBuild / githubWorkflowTargetTags :=
  "v0.1*" ::
    Nil

ThisBuild / githubWorkflowPublishTargetBranches :=
  StartsWith(Tag("v0.1")) ::
    Nil

ThisBuild / githubWorkflowPublish := {
  val ciRelease = Sbt(
    commands = List("ci-release"),
    name = Some("Publish project artifacts."),
  )

  ciRelease ::
    Nil
}
