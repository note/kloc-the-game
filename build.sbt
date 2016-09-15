scalaVersion := "2.11.8"

lazy val rules = project

lazy val web = project.dependsOn(rules).enablePlugins(PlayScala).settings(jasmineSettings : _*)
