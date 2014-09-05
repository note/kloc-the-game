scalaVersion := "2.11.1"

lazy val rules = project

lazy val web = project.dependsOn(rules).enablePlugins(PlayScala)
