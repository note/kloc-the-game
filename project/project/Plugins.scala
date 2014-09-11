import sbt._

object Plugins extends Build {
  lazy val plugins = Project("root", file("."))
    .dependsOn(uri("git://github.com/note/sbt-jasmine-plugin.git#update-jasmine"))
}
