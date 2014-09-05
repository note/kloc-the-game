lazy val rules = project

lazy val web = project.dependsOn(rules).enablePlugins(PlayScala)
