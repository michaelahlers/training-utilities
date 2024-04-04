enablePlugins(NativeImagePlugin)

Compile / mainClass := Some("ahlers.training.tools.ToolsCliApp")

nativeImageVersion  := "21.0.2"
nativeImageJvm      := "graalvm-java21"
nativeImageJvmIndex := "cs"
