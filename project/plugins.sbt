resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

// sbt-idea plugin
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")
