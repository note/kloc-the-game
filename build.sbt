scalaVersion := "2.11.1"

sbtVersion := "0.13.5"

lazy val rules = project

lazy val web = project.dependsOn(rules).enablePlugins(PlayScala)
