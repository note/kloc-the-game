name := """web"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  cache,
  specs2 % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.4.10" % Test,
  "org.asynchttpclient" % "async-http-client" % "2.0.15" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

appJsDir <+= Def.setting { baseDirectory.value / "public" / "javascripts" }

appJsLibDir <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "libs" }

jasmineTestDir <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "tests" }

jasmineConfFile <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "tests" / "test.dependencies.js" }

jasmineRequireJsFile <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "libs" / "require-2.1.15.js" }

jasmineRequireConfFile <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "tests" / "require.conf.js" }

(test in Test) <<= (test in Test) dependsOn (jasmine)
