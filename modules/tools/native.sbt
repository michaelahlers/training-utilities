enablePlugins(GraalVMNativeImagePlugin)
enablePlugins(JavaAppPackaging)

graalVMNativeImageOptions :=
  "--allow-incomplete-classpath" ::
    "--report-unsupported-elements-at-runtime" ::
    "--initialize-at-build-time" ::
    "--no-fallback" ::
    Nil

GraalVMNativeImage / mainClass := Some("ahlers.training.tools.ToolsCliApp")
