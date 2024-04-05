/**
 * @see [[https://github.com/scalameta/sbt-native-image]]
 * @see [[https://www.vandebron.tech/blog/building-native-images-and-compiling-with-graalvm-and-sbt]]
 */
enablePlugins(NativeImagePlugin)

Compile / mainClass := Some("ahlers.training.tools.ToolsCliApp")

/**
 * Default settings refer to an outdated JDK.
 * These choose more modern values, and fix compilation.
 *
 * @see [[https://github.com/coursier/jvm-index/blob/master/indices]]
 */
nativeImageVersion  := "21.0.2"
nativeImageJvm      := "graalvm-java21"
nativeImageJvmIndex := "cs"
